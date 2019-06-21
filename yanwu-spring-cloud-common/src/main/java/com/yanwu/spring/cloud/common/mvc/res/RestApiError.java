package com.yanwu.spring.cloud.common.mvc.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
@JsonInclude(Include.NON_NULL)
public class RestApiError {

	public final static String CODE_OK="0";
	
	public final static String MESSAGE_SUCCEED="";

	public final static String DEFAULT_ERROR_PARAMS="";
/**
	 * HTTP Status
	 */
	private int status;
	
	/**
	 * Business Code
	 */
	private String code;

	/**
	 * A code that the server uses as a key to retrieve the localized message.
	 * reserved
	 */
	private String messageKey;

	/**
	 * The localized message with substituted parameters.
	 */
	private String message;

	/**
	 * Raw error message, which is not localized. It is for debugging purposes.
	 */
	private String rawMessage;

	/**
	 * map of parameters that the client can use to generate it's own error
	 * message if needed
	 */
	private Map<String, Object> errorParams;

	/**
	 * map of validation errors <BR>
	 * key: path of the invalid value in the request. <BR>
	 * value: collection of messages.properties indicating the errors for that
	 * field/object.<BR>
	 * Ex: A JSON representation of validationErrors looks like this: <BR>
	 * {<BR>
	 * "firewallRules[0].destinationMac.entries[0].tags[2].value": ["size must
	 * be between 0 and 64"],<BR>
	 * "firewallRules[0].destinationMac.entries[1].description": ["cannot be
	 * null or empty"],<BR>
	 * "firewallRules[0].destinationMac.entries[1].somefield": [<BR>
	 * "size must be less than 128",<BR>
	 * "cannot use special chars (*,$,%)"<BR>
	 * ]<BR>
	 * }<BR>
	 */
	private Map<String, Collection<String>> validationErrors;

	/**
	 * Typically a link to a document where more information can be accesses -
	 * hivenation/helpdoc etc.
	 */
	private String moreInfo;
}