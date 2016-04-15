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

		if ( value == "" || value == null || value == "null" || value == undefined )
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
		if(sansServerUtility.isBlank(value))
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
	
	/*
	 * Wrapper function to execute lambda transactions
	 */
	this.lambdaCall = function(functionName, requestData, successFunction, errorFunction)
	{
		var awsLambda = new AWS.Lambda();
		
		AWS.config.credentials.get(function(err)
		{
	    	if(err)
	    	{
	    		console.log(err);
	    		errorFunction(err);
	    	}
	    	else
	    	{
	    		awsLambda.invoke(
					{
						FunctionName: functionName,
		        		Payload: JSON.stringify(requestData)
		        	},
		        	function(err, data)
		        	{
		        		if (err)
		        		{
		        			console.log(err);
		        			errorFunction(err);
		        		}
		        		else
		        		{
		        			var output = JSON.parse(data.Payload);
		        			
		        			successFunction(output);
		        		}
		        	}
		    	);
	    	}
	    });
	}
	
	/**
	 * The navigateTo function will assist in the page transitions by adding a loading gif while the page loads.
	 *
	 * @param id
	 */
	this.navigateTo = function(router, rashRoute, silentMode)
	{
		// If we are not in silent mode, display the processing image
		if(sansServerUtility.isBlank(silentMode))
		{
			// Swap the content wrapper out with the loading image
			$("#ApplicationContent").html("<div style='min-height: 500px;' class='panel flex-panel'><div id='pageLoadingPanel' class='loadingPanel'><img src='static/images/loading-image.gif'/></div></div>");

			// Navigate to out content page
			router.navigate(rashRoute);
		}
		else
		{
			// Navigate without the processing image since we are not leaving the page
			router.navigate(rashRoute, silentMode);
		}
	}
}
