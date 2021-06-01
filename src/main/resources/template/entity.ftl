package ${pkg};

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
<#list imports as impt>
import ${impt};
</#list>

@TableName("${tableName}")
@Data
public class ${className}{

<#list fields as field>
    <#if field.name == "id">
    @TableId
    private ${field.type} ${field.name};

    <#else>
    private ${field.type} ${field.name};

    </#if>

</#list>
}