define([
        'js/kendo-template!app/views/hub/HomeView'], function(
        		pageTemplate)
{ 
	homeObject = new function()
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
    	homeObject.initPage();
    }
     
    return {
        start:start
    };
});
