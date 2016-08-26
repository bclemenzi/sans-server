define(["kendo.all.min"], function(kendo)
{
	m_applicationRouter = new kendo.Router();

    function startRouting()
    {
    	/* MAIN MENU ROUTING */
    	m_applicationRouter.route("/", function() {
        	loadController("hub/EditController");
    	});
    	
    	m_applicationRouter.route("editUser/:userId", function(userId) {
        	var parameters = {
        		userId: userId
    		};

        	loadController("user/EditUserController", parameters);
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
