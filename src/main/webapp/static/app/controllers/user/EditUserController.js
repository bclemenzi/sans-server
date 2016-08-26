define([
        'js/sansserver-jquery-expand',
        'js/style-importer!css/forms',
        'js/kendo-template!app/views/user/EditUserView'], function(
        		sansServerExpand,
        		formsStyle,
        		pageTemplate)
{ 
	editUserObject = new function()
	{
		var userId;
		var formValidator;
		
		this.initPage = function(parameters)
		{
			editUserObject.userId = parameters.userId;
			
			$("#editorPageCancelButton").click(function(){
				sansServerUtility.closeFullPageDialog("EditorWorkspace");
			});
			
			$("#editorPageSaveButton").click(function(){
				editUserObject.submitForm();
			});
			$("#editorPageSaveButton").kDisable();
			
			$("#username").useMaxLength();
			$("#username").emailAddressEntry();
			$("#fullName").useMaxLength();
			
			$("#apiEndPoint").text(globalAppConfig.baseServiceUrl);
			
			sansServerUtility.lambdaCall(
				globalAppConfig.environmentPrefix + 'ViewUser', 
				{
					userId: editUserObject.userId
				}, 
				editUserObject.loadUserData, 
				editUserObject.onLoadError
			);
			
			editUserObject.formValidator = $("#pageContentForm").kendoValidator().data("kendoValidator");
		}
		
		this.loadUserData = function(output)
		{
			$("#pageContentLoading").hide();
			$("#pageContentForm").show();
			
			$("#editorPageSaveButton").kEnable();
			
			$("#username").val(output.data.user.username);
			$("#fullName").val(output.data.user.fullName);
			$("#apiKey").text(output.data.user.apiKey);
			$("#active").prop("checked", output.data.user.active);
		}
		
		this.onLoadError = function(err)
		{
			sansServerUtility.alertDialogWithTitle("An error occured while loading page data, Please cancel and try again.", "Error");
		}
		
		this.submitForm = function()
		{
			if(editUserObject.formValidator.validate())
			{
				$("#editorPageCancelButton").kDisable();
				$("#editorPageSaveButton").kDisable();
				
				$("#messagePanel").text("Please wait...");
				$("#messagePanel").fadeIn(500);
				
				sansServerUtility.lambdaCall(
					globalAppConfig.environmentPrefix + 'UpdateUser', 
					{
						userId: editUserObject.userId,
						username: $("#username").val().trim(),
						fullName: $("#fullName").val().trim(),
						active: $('#active').is(':checked')
					}, 
					editUserObject.onFormSuccess, 
					editUserObject.onFormError
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
	
    function start(parameters)
    {
    	var tmpView = new kendo.View(
        	pageTemplate
        );

    	// Push the view to the main content wrapper
    	$("#ApplicationContent").html(tmpView.render());
    	
    	// Init the content page
    	editUserObject.initPage(parameters);
    }
     
    return {
        start:start
    };
});
