define([
        'js/kendo-template!app/views/user/ViewUsersView',
        'app/datasources/UsersDatasource'], function(
        		pageTemplate,
        		usersDatasource)
{ 
	viewUsersObject = new function()
	{
		var usersDS = new kendo.data.DataSource(usersDatasource);
		
		this.initPage = function()
		{
			$("#usersDatagrid").kendoGrid({
				dataSource: usersDS,
	            sortable: true,
	            scrollable: false,
	        	pageable: true,
				columns:[
				{
					field: "fullName", title: "Name", sortable: true
				},
				{
					command: { name: "edit", click: viewUsersObject.editUser }, title: " ", width: "80px", sortable:false
				}]
			});
		}
		
		this.refreshUsers = function(e)
		{
			usersDS.read({});
		}
		
		this.editUser = function(e)
		{
			var itemModel = this.dataItem($(e.currentTarget).closest("tr"));
			
			sansServerUtility.launchFullPageDialog("EditorWorkspace", globalAppConfig.environmentSiteUrl + "/editor.html#editUser/" + itemModel.userId, viewUsersObject.refreshUsers);
			
			// Prevent the click from changing the url hash
			e.preventDefault();
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
