package tl.gov.mci.lis.models.user;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
public class OneTimePassword {
    @NonNull
    private String oneTimePasswordCode;
    @NonNull
    private Instant expires;
}
