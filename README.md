# lis-api
Rest API License System Information

## Report API Filters

The following report endpoints support filtering by Endereco (address) fields:

### Endpoints

- `POST /api/v1/reports/empresas`
- `POST /api/v1/reports/certificados-inscricao-cadastro`
- `POST /api/v1/reports/certificados-licenca-atividade`

### Endereco Filter Parameters

All three endpoints support the following optional filter fields for filtering by address location:

| Field | Type | Description |
|-------|------|-------------|
| `municipioId` | Long | Filter by Municipio ID |
| `postoAdministrativoId` | Long | Filter by Posto Administrativo ID |
| `sucoId` | Long | Filter by Suco ID |

These filters are independent and can be combined. When multiple filters are provided, they are applied conjunctively (AND logic).

### Example Request

```json
POST /api/v1/reports/empresas
{
  "municipioId": 1,
  "postoAdministrativoId": 2,
  "sucoId": 3
}
```

### Filter Combinations

- Only `municipioId`: Returns all records within the specified Municipio
- `municipioId` + `postoAdministrativoId`: Returns records within the specified Posto Administrativo of the Municipio
- `municipioId` + `postoAdministrativoId` + `sucoId`: Returns records within the specified Suco
- Filters are optional - omitting them returns all records matching other criteria
