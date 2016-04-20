(function($, kendo, _) {
  "use strict";

  // add a backbone namespace if we need it
  kendo.AwsLambda = kendo.AwsLambda || {};

  var AwsLambdaTransport = function(awsCredentials){
    this._awsCredentials = awsCredentials;
  };
  
  // add basic CRUD operations to the transport
  _.extend(AwsLambdaTransport.prototype, {

    create: function(options) {
    	console.log("AwsLambda - create");
    },

    read: function(options) {
    	console.log("AwsLambda - read");
    },

    update: function(options) {
    	console.log("AwsLambda - update");
    },

    destroy: function(options) {
    	console.log("AwsLambda - destory");
    }
  });

  // Define the custom data source that uses a AwsLambda.awsCredentials
  // as the underlying data store / transport
  kendo.AwsLambda.DataSource = kendo.data.DataSource.extend({
    init: function(options) {
      var awsltrans = new AwsLambdaTransport(options.awsCredentials);
      _.defaults(options, {transport: awsltrans, autoSync: true});

      kendo.data.DataSource.fn.init.call(this, options);
    }
  }); 

})($, kendo, _);