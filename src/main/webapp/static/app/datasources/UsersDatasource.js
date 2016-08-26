define([], function(){
	 
	var userModel = kendo.data.Model.define({
		id: "id"
	});
	
	var usersDatasource = {
		transport: {
			read: {
				url: globalAppConfig.baseServiceUrl + "/getUsers",
				cache: false,
				dataType: "json",
				contentType: "application/json",
				type: "POST"
			}
		},
		batch: false,
		serverPaging: true,
		serverSorting: true,
		pageSize: 12,
		schema: {
			model: userModel,
			data: "data.users",
			total: "data.pageTotal"
		}
	};
     
    return usersDatasource;
});