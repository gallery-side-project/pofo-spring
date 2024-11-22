package org.pofo.infra.elasticsearch.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;

@Builder
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(indexName = "stack-auto-complete", writeTypeHint = WriteTypeHint.FALSE)
@Mapping(mappingPath = "elastic/tech-mapping.json")
@Setting(settingPath = "elastic/tech-setting.json")
public class TechStackAutoComplete {

    @Id
    private String id;

    @CompletionField(maxInputLength = 100)
    private Completion suggest;

    private String name;
}
