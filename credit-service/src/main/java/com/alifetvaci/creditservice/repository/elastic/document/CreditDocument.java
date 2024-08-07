package com.alifetvaci.creditservice.repository.elastic.document;

import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@Document(indexName = "credit")
@AllArgsConstructor
@NoArgsConstructor
public class CreditDocument{

    @Id
    private int id;

    private CreditStatus status;

    private BigDecimal amount;

    private String identificationNumber;

    private boolean deleted;

}
