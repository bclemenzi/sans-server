define([
        'js/sansserver-jquery-expand',
        'js/style-importer!css/forms',
        'js/kendo-template!app/views/hub/EditView'], function(
        		sansServerExpand,
        		formsStyle,
        		pageTemplate)
{ 
	editObject = new function()
	{
		this.initPage = function()
		{
			$("#editorPageCancelButton").click(function(){
				sansServerUtility.closeFullPageDialog("EditorWorkspace");
			});
			
			$("#editorPageSaveButton").click(function(){
				editObject.submitForm();
			});
		}
		
		this.submitForm = function()
		{
			alert("Feature not yet implemented.");
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
    	editObject.initPage();
    }
     
    return {
        start:start
    };
});
