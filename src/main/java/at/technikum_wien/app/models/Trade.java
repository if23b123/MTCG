package at.technikum_wien.app.models;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

@Getter
@Setter
public class Trade {
    @JsonAlias({"Id"})
    private String id;

    private String dealCreatorUsername;
    @JsonAlias({"CardToTrade"})
    private String cardIdToTrade;
    @JsonAlias({"Type"})
    private String wantedType;
    @JsonAlias({"MinimumDamage"})
    private Integer minDamage;

    private String traderUsername;
    private String offeredCardId;


}
