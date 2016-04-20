define(['kendo/js/kendo.router.min',
        'kendo/js/kendo.data.min',
        'kendo/js/kendo.tabstrip.min',
        'kendo/js/kendo.tooltip.min',
        'kendo/js/kendo.validator.min',
        'kendo/js/kendo.toolbar.min',
        'kendo/js/kendo.multiselect.min',
        'kendo/js/kendo.autocomplete.min',
        'kendo/js/kendo.numerictextbox.min',
        'kendo/js/kendo.grid.min',
        'kendo/js/kendo.listview.min',
        'kendo/js/kendo.view.min'], function()
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
