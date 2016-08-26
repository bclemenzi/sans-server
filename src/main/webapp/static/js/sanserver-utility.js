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
		        			console.log(output);
		        			
		        			successFunction(output);
		        		}
		        	}
		    	);
	    	}
	    });
	}
	
	/**
	 * The alertDialog function is used to launch a generic alert dialog box with a given message.
	 *
	 * @param {String} alertMessage
	 */
	this.alertDialog = function(alertMessage)
	{
		sansServerUtility.alertDialogWithTitle(alertMessage,'Message');
	}

	/**
	 * The alertDialogWithTitle function is used to launch a generic alert dialog box with a given message and a given title.
	 *
	 * @param {String} alertMessage
	 * @param {String} dialogTitleStr  title for the pop up window
	 */
	this.alertDialogWithTitle = function(alertMessage, dialogTitleStr)
	{
		var content =
					  "<div class='alert-dialog'>" +
					  "   <p class='alert-dialog-message'>" + alertMessage + "</p>" +
					  "   <div class='alert-dialog-button-bar'>" +
					  "     <a class='alert-dialog-ok-button k-button k-button-icontext'>OK</a>"
					  "   </div>" +
					  "</div>";

		var kendoWindow = $("<div></div>").kendoWindow(
		{
			close: function(){kendoWindow.data("kendoWindow").destroy()},
			draggable: false,
			height: 150,
			width: 400,
			title: dialogTitleStr,
			modal: true
		});

		kendoWindow.data("kendoWindow").content(content).center().open();

		kendoWindow.find(".og-alert-dialog-ok-button, .k-button").click(function(e)
		{
			kendoWindow.data("kendoWindow").close();

			// Prevent the click from changing the url hash
			e.preventDefault();
		});
	}
	
	this.confirmationDialog = function(alertMessage, confirmFunction, denyFunction, functionData)
	{
		var content =
			"<p>"+alertMessage+"</p>"+
			"<button id='delete-yes' class='k-button og-button'>" + $.i18n.t('yes') + "</button>&nbsp;"+
			"<button id='delete-no' class='k-button og-button'>" + $.i18n.t('no') + "</button>";

		var kendoWindow = $("<div></div>").kendoWindow({
				width: "410px",
				height: "160px",
				title: $.i18n.t('confirmAction'),
				resizable: false,
				modal: true,
				close: function(){kendoWindow.data("kendoWindow").destroy()},
			});

		kendoWindow.data("kendoWindow")
			.content(content)
			.center().open();

		kendoWindow
			.find("#delete-yes,#delete-no")
			.click(function(e)
			{
				if ($(this).attr('id')=="delete-yes")
				{
					var id = $(this).attr('id');
					confirmFunction(functionData);
				}
				else
				{
					var id = $(this).attr('id');
					if(typeof denyFunction !== 'undefined')
					{
						denyFunction(functionData);
					}
				}

				kendoWindow.data("kendoWindow").close();

				// Prevent the click from changing the url hash
				e.preventDefault();
			})
			.end();
	}

	this.confirmationDialogWithTitle = function(dialogTitleStr, alertMessage, confirmFunction, denyFunction, functionData)
	{
		var content =
			"<p>"+alertMessage+"</p>"+
			"<button id='delete-yes' class='k-button og-button'>" + $.i18n.t('yes') + "</button>&nbsp;"+
			"<button id='delete-no' class='k-button og-button'>" + $.i18n.t('no') + "</button>";

		var kendoWindow = $("<div></div>").kendoWindow({
				width: "410px",
				height: "160px",
				title: dialogTitleStr,
				resizable: false,
				modal: true,
				close: function(){kendoWindow.data("kendoWindow").destroy()},
			});

		kendoWindow.data("kendoWindow")
			.content(content)
			.center().open();

		kendoWindow
			.find("#delete-yes,#delete-no")
			.click(function(e)
			{
				if ($(this).attr('id')=="delete-yes")
				{
					var id = $(this).attr('id');
					confirmFunction(functionData);
				}
				else
				{
					var id = $(this).attr('id');
					if(typeof denyFunction !== 'undefined')
					{
						denyFunction(functionData);
					}
				}

				kendoWindow.data("kendoWindow").close();

				// Prevent the click from changing the url hash
				e.preventDefault();
			})
			.end();
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
	
	/**
	 * The launchFullPageDialog function is used to launch a full page dialog to render a secondary navigation panel.
	 *
	 * @param {String} pageUrl
	 * @param {String} callbackFunction
	 */
	this.launchFullPageDialog = function(id, pageUrl, callbackFunction)
	{
		var kendoWindow = $("<div id='" + id + "' class='navigationFullPageDialog' style='padding:0;'></div>").kendoWindow(
		{
			close: function()
			{
				try
				{
					// Execute the callback function
					callbackFunction();
				}
				catch(err)
				{
					console.log("Error executing launchFullPageDialog callbackFunction");
					console.log(err);
				}
				
				kendoWindow.data("kendoWindow").destroy();
			},
			animation:
			{
			    open: false
			},
			iframe: true,
			draggable: false,
			resizable: false,
			modal: true,
			title: false,
			height:'99%'
		});
		
		kendoWindow.data("kendoWindow").refresh({url: pageUrl});
		
		var iframeDomElement = kendoWindow.children("iframe")[0];
		iframeDomElement.setAttribute("allowfullscreen","");
		kendoWindow.data("kendoWindow").open();
		kendoWindow.data("kendoWindow").maximize();
		
		console.log("launchFullPageDialog (kendoWindow): " + id);
	}
	
	/**
	 * The closeFullPageDialog will close the full page dialog box launched form the parent window
	 *
	 * @param id
	 */
	this.closeFullPageDialog = function(id)
	{
		try
		{
			window.parent.$("#" + id).data("kendoWindow").close();
		}
		catch(err)
		{
			console.log(err);
			console.log("closeFullPageDialog (window.close): " + id);
			window.close();
		}
	}
}
