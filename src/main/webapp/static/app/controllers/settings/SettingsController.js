define([
        'js/sansserver-jquery-expand',
        'js/style-importer!css/forms',
        'js/kendo-template!app/views/settings/SettingsView'], function(
        		sansServerExpand, 
        		fomrsStyle,
        		pageTemplate)
{ 
	settingsObject = new function()
	{
		var formValidator;
		
		this.initPage = function()
		{
			$("#saveSettingsButton").click(function(){
				settingsObject.saveUserSettings();
			});
			
			$("#saveSettingsButton").kDisable();
			
			sansServerUtility.lambdaCall(
				globalAppConfig.environmentPrefix + 'ViewAuthenticatedUser', 
				{}, 
				settingsObject.loadUserData, 
				settingsObject.onError
			);
		}
		
		this.loadUserData = function(output)
		{
			$("#saveSettingsButton").kEnable();
			
			$("#emailAddress").text(output.data.user.username);

			settingsObject.formValidator = $("#userAccountSettingsForm").kendoValidator(
	 		{
	 			rules:
	 			{
	 				oldpasswordconfirmation: function(input) 
					{
						var ret = true;
						if(input.is("[id=oldPassword]")) 
						{
							if (sansServerUtility.isBlank($("#oldPassword").val()))
							{
								ret = false
							}							
						}
						return ret;
					},
	 				passwordcharconfirmation: function(input) 
	 				{
	 					var ret = true;
						if(input.is("[id=newPassword]")) 
	 					{
							if ($("#newPassword").val().length > 5)
							{
								ret = settingsObject.isValidPasswordChars($("#newPassword").val());
							}							
	 					}
	 					return ret;
	 				},
	 				passwordemptyconfirmation: function(input) 
	 				{
	 					var ret = true;
						if(input.is("[id=newPassword]")) 
	 					{
							if ($("#newPassword").val().length < 6)
							{
								ret = false;
							}							
	 					}
	 					return ret;
	 				},
	 				passwordconfirmation: function(input) 
	 				{
	 					var ret = true;
						if(input.is("[id=confirmPassword]")) 
	 					{
							if (($("#confirmPassword").val() != $("#newPassword").val()))
							{
								ret = false;
							}	
	 					}
	 					return ret;
	 				}
	 			},
	 			messages:
	 			{
	 				oldpasswordconfirmation: "Current password is required.",
	 				passwordcharconfirmation: "Password contains invalid characters.",
	 				passwordemptyconfirmation: "Password requires minimum 6 characters.",
	 				passwordconfirmation: "Password fields must match."
	 			}
	 		}).data("kendoValidator");	
		}
		
		this.isValidPasswordChars = function(str) 
		{
			return  /^[\040-\176]*$/.test(str);
		}
		
		this.saveUserSettings = function()
		{
			if(settingsObject.formValidator.validate()) 
			{
				$("#saveSettingsButton").kDisable();
				
				sansServerUtility.lambdaCall(
					globalAppConfig.environmentPrefix + 'UpdateUserPassword', 
					{
						oldPassword: $("#oldPassword").val(),
					    newPassword: $("#newPassword").val()
					}, 
					settingsObject.onSaveSuccess, 
					settingsObject.onError
				);
			}
		}
		
		this.onSaveSuccess = function(output)
		{
			$("#oldPassword").val("");
			$("#newPassword").val("");
			$("#confirmPassword").val("");
			
			$("#saveSettingsButton").kEnable();
			
			alert("Your user settings have been saved");
		}
		
		this.onError = function(err)
		{
			$("#saveSettingsButton").kEnable();
			
			alert("There was an error while saving your settings");
		}
	}
	
    function start()
    {
    	var tmpView = new kendo.View(
        	pageTemplate
        );

    	// Push the view to the main content wrapper
    	$("#ApplicationContent").html(tmpView.render());
    	
    	// Init the content page
    	settingsObject.initPage();
    }
     
    return {
        start:start
    };
});
