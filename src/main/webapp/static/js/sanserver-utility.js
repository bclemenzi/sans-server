/*!
 * JavaScript SansServer Utility v1.0.0
 * https://github.com/bclemenzi/sans-server
 *
 * Copyright 20126 NFB Software
 * Released under the GNU GENERAL PUBLIC LICENSE
 */
var sansServerUtility = new function()
{
	/*
	 * Returns true or false if the string object passed in is blank
	 */
	this.isBlank = function(value)
	{
		var ret = false;

		if ( value === "" || value == null || value === "null" || value == undefined )
		{
			ret = true;
		}

		return ret;
	}

	/*
	 * Returns an empty string if the value passed in is null or undefined
	 */
	this.emptyIfNull = function(value)
	{
		if(value == null || value === "null" || value == undefined)
		{
			return "";
		}
		else
		{
			return value;
		}
	}
	
	/*
	 * Returns the default string if the test value is blank
	 */
	this.replaceIfNull = function(value, defaultValue)
	{
		if(sansServerUtility.isBlank(value))
		{
			return defaultValue;
		}
		else
		{
			return value;
		}
	}
}