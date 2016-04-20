define([], function(){
 
	var userModel = kendo.data.Model.define({
		id: "id"
	});
	
	var userFilesListDatasource = {
		awsCredentials: AWS.config.credentials,
		transport: {
			read: {
				url: "api/viewUsers",
				functionName: "viewUsers",
				cache: false,
				dataType: "json"
			}
		},
		batch: false,
		serverPaging: true,
		serverSorting: true,
		pageSize: 12,
		schema: {
			model: userModel,
			data: "users",
			total: "pageTotal"
		}
	};
     
    return userModel;
});