define([
        'js/kendo-template!app/views/settings/SettingsView'], function(
        		pageTemplate)
{ 
	settingsObject = new function()
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
    	settingsObject.initPage();
    }
     
    return {
        start:start
    };
});
