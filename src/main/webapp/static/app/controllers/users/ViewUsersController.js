define([
        'js/kendo-template!app/views/users/ViewUsersView',
        'app/datasources/UsersDatasource'], function(
        		pageTemplate,
        		usersDatasource)
{ 
	viewUsersObject = new function()
	{
		var usersDS = new kendo.AwsLambda.DataSource(usersDatasource);
		
		this.initPage = function()
		{
			console.log("viewUsersObject before read");
			usersDS.read({});
			console.log("viewUsersObject after read");
		}
		
		this.loadUserData = function()
		{
			console.log("viewUsersObject after read change");
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
