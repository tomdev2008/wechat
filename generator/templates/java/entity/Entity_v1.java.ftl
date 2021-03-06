package ${packageName}.domain;

import java.io.*;
import java.util.*;
import javax.persistence.*;
 
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import com.alibaba.fastjson.*;

import com.glaf.core.base.*;
import com.glaf.core.util.DateUtils;

@Entity
@Table(name = "${tableName}")
public class ${entityName} implements Serializable, JsonReadable {
	private static final long serialVersionUID = 1L;

        @Id
	@Column(name = "${idField.columnName}", length = 50, nullable = false)
	protected ${idField.type} ${idField.name};

	${jpa_fields?if_exists}

	public ${entityName}() {

	}

	public ${idField.type} get${idField.firstUpperName}(){
	    return this.${idField.name};
	}

	${jpa_get_methods?if_exists}


	public void set${idField.firstUpperName}(${idField.type} ${idField.name}) {
	    this.${idField.name} = ${idField.name}; 
	}

	${jpa_set_methods?if_exists}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	public JSONObject toJsonObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("${idField.name}", ${idField.name});
<#if pojo_fields?exists>
    <#list  pojo_fields as field>	
        <#if field.type?exists >
	      <#if ( field.type== 'Integer' || field.type== 'Long' || field.type== 'Double' || field.type== 'Boolean')>
                jsonObject.put("${field.name}", ${field.name});
	      <#elseif ( field.type== 'Date' )>
	        jsonObject.put("${field.name}", DateUtils.getDateTime(${field.name}));
		jsonObject.put("${field.name}_date", DateUtils.getDate(${field.name}));
		jsonObject.put("${field.name}_datetime", DateUtils.getDateTime(${field.name}));
	      <#else>
                if (${field.name} != null) {
		    jsonObject.put("${field.name}", ${field.name});
		} 
	      </#if>
	<#else>
		if (${field.name} != null) {
		    jsonObject.put("${field.name}", ${field.name});
		} 
	</#if>
    </#list>
</#if>
		return jsonObject;
	}

}
