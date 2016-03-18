define([
        'kendo/js/kendo.router',
        'kendo/js/kendo.data',
        'kendo/js/kendo.validator',
        'kendo/js/kendo.listview',
        'kendo/js/kendo.view'], function(
        		kRouter,
        		kData,
        		kValidator,
        		kListView,
        		kView)
{
	m_applicationRouter = new kendo.Router();

    function startRouting()
    {
    	/* MAIN MENU ROUTING */
    	m_applicationRouter.route("/", function() {
        	loadController("hub/HomeController");
    	});
    	m_applicationRouter.route("home", function() {
        	loadController("hub/HomeController");
    	});
    	m_applicationRouter.route("users", function() {
        	loadController("users/ViewUsersController");
    	});
    	m_applicationRouter.route("settings", function() {
        	loadController("settings/SettingsController");
    	});

    	// Start our main router
    	m_applicationRouter.start();
    }

    function loadController(controllerName, parameters)
    {
        require(['app/controllers/' + controllerName], function(controller){
            controller.start(parameters);
        });
    }

    return {
        startRouting:startRouting
    };
});
