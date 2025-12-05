# Report Feature - Functional Specification

## Overview

The Report feature allows authorized users (ADMIN, MANAGER, CHIEF, STAFF) to generate paginated reports for the following entities:
- **Empresa** (Company)
- **Aplicante** (Applicant)
- **CertificadoInscricaoCadastro** (Registration Certificate)
- **CertificadoLicencaAtividade** (Activity License Certificate)

All report endpoints support flexible filtering based on entity properties, including child/related entity properties.

---

## API Endpoints

### Base URL
```
/api/v1/reports
```

### Authentication
All endpoints require authentication with one of the following roles:
- `ROLE_ADMIN`
- `ROLE_MANAGER`
- `ROLE_CHIEF`
- `ROLE_STAFF`

---

## 1. Empresa Report

### Endpoint
```
POST /api/v1/reports/empresas
```

### Query Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 50 | Number of items per page |

### Request Body (Optional)
```json
{
  "nome": "string",
  "nif": "string",
  "numeroRegistoComercial": "string",
  "telefone": "string",
  "telemovel": "string",
  "email": "string",
  "tipoPropriedade": "PUBLICA | PRIVADA | COOPERATIVA | OUTRA",
  "tipoEmpresa": "MICROEMPRESA | PEQUENA | MÉDIA | GRANDE",
  "dataRegistoFrom": "2024-01-01",
  "dataRegistoTo": "2024-12-31",
  "capitalSocialMin": 1000.00,
  "capitalSocialMax": 100000.00,
  "totalTrabalhadoresMin": 1,
  "totalTrabalhadoresMax": 100,
  "sociedadeComercialId": 1
}
```

### Filter Fields
| Field | Type | Filter Type | Description |
|-------|------|-------------|-------------|
| `nome` | String | LIKE (case-insensitive) | Company name |
| `nif` | String | LIKE (case-insensitive) | Tax identification number |
| `numeroRegistoComercial` | String | LIKE (case-insensitive) | Commercial registry number |
| `telefone` | String | LIKE | Phone number |
| `telemovel` | String | LIKE | Mobile phone |
| `email` | String | LIKE (case-insensitive) | Email address |
| `tipoPropriedade` | Enum | EXACT | Property type |
| `tipoEmpresa` | Enum | EXACT | Company type/size |
| `dataRegistoFrom` | LocalDate | >= | Registration date from |
| `dataRegistoTo` | LocalDate | <= | Registration date to |
| `capitalSocialMin` | Double | >= | Minimum social capital |
| `capitalSocialMax` | Double | <= | Maximum social capital |
| `totalTrabalhadoresMin` | Long | >= | Minimum number of workers |
| `totalTrabalhadoresMax` | Long | <= | Maximum number of workers |
| `sociedadeComercialId` | Long | EXACT | Commercial society ID (relationship) |

### Response
```json
{
  "content": [
    {
      "id": 1,
      "nome": "Example Company",
      "nif": "123456789",
      "numeroRegistoComercial": "RC001",
      ...
    }
  ],
  "pageable": { ... },
  "totalElements": 100,
  "totalPages": 2,
  ...
}
```

---

## 2. Aplicante Report

### Endpoint
```
POST /api/v1/reports/aplicantes
```

### Query Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 50 | Number of items per page |

### Request Body (Optional)
```json
{
  "tipo": "CADASTRO | ATIVIDADE",
  "categoria": "COM | IND | COMERCIAL | INDUSTRIAL",
  "numero": "string",
  "estado": "APROVADO | REJEITADO | EM_CURSO | SUBMETIDO | ATRIBUIDO | REVISAO | REVISTO | SUSPENDE | EXPIRADO",
  "empresaId": 1,
  "direcaoId": 1,
  "createdAtFrom": "2024-01-01T00:00:00Z",
  "createdAtTo": "2024-12-31T23:59:59Z"
}
```

### Filter Fields
| Field | Type | Filter Type | Description |
|-------|------|-------------|-------------|
| `tipo` | Enum | EXACT | Application type |
| `categoria` | Enum | EXACT | Category |
| `numero` | String | LIKE (case-insensitive) | Application number |
| `estado` | Enum | EXACT | Application status |
| `empresaId` | Long | EXACT | Company ID (relationship) |
| `direcaoId` | Long | EXACT | Assigned direction ID (relationship) |
| `createdAtFrom` | Instant | >= | Creation date from |
| `createdAtTo` | Instant | <= | Creation date to |

---

## 3. Certificado Inscrição Cadastro Report

### Endpoint
```
POST /api/v1/reports/certificados-inscricao-cadastro
```

### Query Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 50 | Number of items per page |

### Request Body (Optional)
```json
{
  "sociedadeComercial": "string",
  "numeroRegistoComercial": "string",
  "atividade": "string",
  "dataValidade": "2025-12-31",
  "dataEmissao": "2024-01-01",
  "nomeDiretorGeral": "string",
  "aplicanteId": 1,
  "createdAtFrom": "2024-01-01T00:00:00Z",
  "createdAtTo": "2024-12-31T23:59:59Z"
}
```

### Filter Fields
| Field | Type | Filter Type | Description |
|-------|------|-------------|-------------|
| `sociedadeComercial` | String | LIKE (case-insensitive) | Commercial society |
| `numeroRegistoComercial` | String | LIKE (case-insensitive) | Commercial registry number |
| `atividade` | String | LIKE (case-insensitive) | Activity description |
| `dataValidade` | String | EXACT | Expiration date |
| `dataEmissao` | String | EXACT | Issue date |
| `nomeDiretorGeral` | String | LIKE (case-insensitive) | Director General name |
| `aplicanteId` | Long | EXACT | Applicant ID (via pedidoInscricaoCadastro relationship) |
| `createdAtFrom` | Instant | >= | Creation date from |
| `createdAtTo` | Instant | <= | Creation date to |

---

## 4. Certificado Licença Atividade Report

### Endpoint
```
POST /api/v1/reports/certificados-licenca-atividade
```

### Query Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 50 | Number of items per page |

### Request Body (Optional)
```json
{
  "sociedadeComercial": "string",
  "numeroRegistoComercial": "string",
  "nif": "string",
  "nivelRisco": "BAIXO | MEDIO | ALTO",
  "atividade": "string",
  "atividadeCodigo": "string",
  "dataValidade": "2025-12-31",
  "dataEmissao": "2024-01-01",
  "nomeDiretorGeral": "string",
  "aplicanteId": 1,
  "createdAtFrom": "2024-01-01T00:00:00Z",
  "createdAtTo": "2024-12-31T23:59:59Z"
}
```

### Filter Fields
| Field | Type | Filter Type | Description |
|-------|------|-------------|-------------|
| `sociedadeComercial` | String | LIKE (case-insensitive) | Commercial society |
| `numeroRegistoComercial` | String | LIKE (case-insensitive) | Commercial registry number |
| `nif` | String | LIKE (case-insensitive) | Tax identification number |
| `nivelRisco` | Enum | EXACT | Risk level |
| `atividade` | String | LIKE (case-insensitive) | Activity description |
| `atividadeCodigo` | String | LIKE (case-insensitive) | Activity code |
| `dataValidade` | String | EXACT | Expiration date |
| `dataEmissao` | String | EXACT | Issue date |
| `nomeDiretorGeral` | String | LIKE (case-insensitive) | Director General name |
| `aplicanteId` | Long | EXACT | Applicant ID (via pedidoLicencaAtividade relationship) |
| `createdAtFrom` | Instant | >= | Creation date from |
| `createdAtTo` | Instant | <= | Creation date to |

---

## Usage Examples

### Example 1: Filter Empresas by type and size
```bash
curl -X POST "http://localhost:8080/api/v1/reports/empresas?page=0&size=10" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "tipoEmpresa": "PEQUENA",
    "totalTrabalhadoresMin": 5
  }'
```

### Example 2: Get all Aplicantes in APROVADO status
```bash
curl -X POST "http://localhost:8080/api/v1/reports/aplicantes" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "estado": "APROVADO"
  }'
```

### Example 3: Filter certificates by activity
```bash
curl -X POST "http://localhost:8080/api/v1/reports/certificados-licenca-atividade" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "atividade": "comercio",
    "nivelRisco": "BAIXO"
  }'
```

### Example 4: Get all records without filters
```bash
curl -X POST "http://localhost:8080/api/v1/reports/empresas" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

## Technical Implementation

### Components Created
1. **Filter DTOs** (`tl.gov.mci.lis.dtos.report`)
   - `EmpresaReportFilter`
   - `AplicanteReportFilter`
   - `CertificadoInscricaoCadastroReportFilter`
   - `CertificadoLicencaAtividadeReportFilter`

2. **JPA Specifications** (`tl.gov.mci.lis.specifications`)
   - `EmpresaSpecification`
   - `AplicanteSpecification`
   - `CertificadoInscricaoCadastroSpecification`
   - `CertificadoLicencaAtividadeSpecification`

3. **Service** (`tl.gov.mci.lis.services.report`)
   - `ReportService`

4. **Controller** (`tl.gov.mci.lis.controllers.report`)
   - `ReportController`

### Repository Changes
All related repositories now extend `JpaSpecificationExecutor` to support dynamic filtering:
- `EmpresaRepository`
- `AplicanteRepository`
- `CertificadoInscricaoCadastroRepository`
- `CertificadoLicencaAtividadeRepository`

---

## Notes
- All filter fields are optional. If no filters are provided, all records are returned (paginated).
- String filters use case-insensitive partial matching (LIKE '%value%').
- Enum filters require exact match with valid enum values.
- Date range filters can be combined (from and to) or used individually.
- Results are sorted by ID in descending order by default.
