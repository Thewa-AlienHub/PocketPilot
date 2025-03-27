package org.example.pocketpilot.dto.responseDTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoalResponseDTO {
    @Id
    private ObjectId id;
    private ObjectId userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    @JsonIgnore  // This prevents the raw value from being serialized in the JSON response
    private BigDecimal progressPrecentage;

    @JsonProperty("progressPrecentage")
    public String getFormattedProgressPrecentage() {
        return progressPrecentage != null ? String.format("%.2f%%", progressPrecentage) : "0.00%";
    }

}
