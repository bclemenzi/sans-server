define([
        'js/kendo-template!app/views/users/ViewUsersView'], function(
        		pageTemplate)
{ 
	viewUsersObject = new function()
	{
		this.initPage = function()
		{
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
    	viewUsersObject.initPage();
    }
     
    return {
        start:start
    };
});
