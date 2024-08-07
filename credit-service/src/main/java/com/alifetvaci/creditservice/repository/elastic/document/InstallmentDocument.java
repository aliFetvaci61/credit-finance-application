package com.alifetvaci.creditservice.repository.elastic.document;

import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
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
@Document(indexName = "installment")
@AllArgsConstructor
@NoArgsConstructor
public class InstallmentDocument{

    @Id
    private int id;

    private InstallmentStatus status;

    private BigDecimal amount;

    private long dueDate;

    private int creditId;

    private boolean deleted;

}
