define([
        'js/sansserver-jquery-expand',
        'js/style-importer!css/forms',
        'js/kendo-template!app/views/user/AddUserView'], function(
        		sansServerExpand,
        		formsStyle,
        		pageTemplate)
{ 
	addUserObject = new function()
	{
		var userId;
		var formValidator;
		
		this.initPage = function()
		{
			$("#editorPageCancelButton").click(function(){
				sansServerUtility.closeFullPageDialog("EditorWorkspace");
			});
			
			$("#editorPageSaveButton").click(function(){
				addUserObject.submitForm();
			});
			$("#editorPageSaveButton").kDisable();
			
			$("#username").useMaxLength();
			$("#username").emailAddressEntry();
			$("#fullName").useMaxLength();
			
			$("#pageContentLoading").hide();
			$("#pageContentForm").show();
			
			addUserObject.formValidator = $("#pageContentForm").kendoValidator(
	 		{
	 			rules:
	 			{
	 				usernameconfirmation: function(input) 
					{
						var ret = true;
						if(input.is("[id=username]")) 
						{
							if (sansServerUtility.isBlank($("#username").val()))
							{
								ret = false
							}							
						}
						return ret;
					},
					fullnameconfirmation: function(input) 
					{
						var ret = true;
						if(input.is("[id=fullName]")) 
						{
							if (sansServerUtility.isBlank($("#fullName").val()))
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
	 				usernameconfirmation: "Email address is required.",
	 				fullnameconfirmation: "Name is required.",
	 				passwordcharconfirmation: "Password contains invalid characters.",
	 				passwordemptyconfirmation: "Password requires minimum 6 characters.",
	 				passwordconfirmation: "Password fields must match."
	 			}
	 		}).data("kendoValidator");	
		}
		
		this.submitForm = function()
		{
			if(addUserObject.formValidator.validate())
			{
				$("#editorPageCancelButton").kDisable();
				$("#editorPageSaveButton").kDisable();
				
				$("#messagePanel").text("Please wait...");
				$("#messagePanel").fadeIn(500);
				
				sansServerUtility.lambdaCall(
					globalAppConfig.environmentPrefix + 'CreateUser', 
					{
						username: $("#username").val().trim(),
						password: $("#newPassword").val().trim(),
						fullName: $("#fullName").val().trim()
					}, 
					addUserObject.onFormSuccess, 
					addUserObject.onFormError
				);
			}
		}
		
		this.onFormSuccess = function(output)
		{
			$("#messagePanel").text("User has been saved!");
			$("#messagePanel").delay(2000).fadeOut(5000);
			
			sansServerUtility.closeFullPageDialog("EditorWorkspace");
		}
		
		this.onFormError = function(err)
		{
			$("#editorPageCancelButton").kEnable();
			$("#editorPageSaveButton").kEnable();
			
			$("#messagePanel").text("An error occured while saving user record");
			$("#messagePanel").delay(2000).fadeOut(500);
			
			sansServerUtility.alertDialogWithTitle("An error occured while saving user record", "Error");
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
    	addUserObject.initPage();
    }
     
    return {
        start:start
    };
});
