/*!
 * JavaScript SansServer Utility v1.0.0
 * https://github.com/bclemenzi/sans-server
 *
 * Copyright 20126 NFB Software
 * Released under the GNU GENERAL PUBLIC LICENSE
 */
$.fn.extend({
	kDisable: function () 
	{
		this.attr("disabled","disabled");
    },
    kEnable: function () 
	{
    	this.removeAttr("disabled");
    },
    enterKeyPress: function (fnc) 
    {
        return this.each(function () {
            $(this).keypress(function (ev) {
                var keycode = (ev.keyCode ? ev.keyCode : ev.which);
                if (keycode == '13') {
                    fnc.call(this, ev);
                }
            })
        })
    },
    forceLowerCase: function () 
	{
    	$(this).on("change keyup", function() 
		{
			var txt = $(this).val();
			
			$(this).val(txt.toLowerCase());
		});
	},
    forceUpperCase: function () 
	{
    	$(this).on("change keyup", function() 
		{
			var txt = $(this).val();
			
			$(this).val(txt.toUpperCase());
		});
	},
    useMaxLength: function () 
	{
    	$(this).on("change keyup", function() 
		{
			var txt = $(this).val()
			var maxlen = parseInt($(this).attr('maxlength'))
			if (txt.length > maxlen) 
			{
				$(this).val(txt.substr(0, maxlen))
				return false;
			}
		});
	},
    kepressNumericEntry: function ()
    {
        $(this).keyup(function(e)
        {
            var key = e.charCode || e.keyCode || 0;
            return (key == 8 || key == 9 ||key == 46 || (key >= 37 && key <= 40) || (key >= 48 && key <= 57) || (key >= 96 && key <= 105));
        });
    },
    alphaNumericEntry: function  (p) {
        var input = $(this),
            az = "abcdefghijklmnopqrstuvwxyz",
            options = $.extend({
                ichars: '!@#$%^&*()+=[]\\\';,/{}|":<>?~`.- _',
                nchars: '',
                allow: ''
            }, p),
            s = options.allow.split(''),
            i = 0,
            ch,
            regex;

        for (i; i < s.length; i++) {
            if (options.ichars.indexOf(s[i]) != -1) {
                s[i] = '\\' + s[i];
            }
        }

        if (options.nocaps) {
            options.nchars += az.toUpperCase();
        }
        if (options.allcaps) {
            options.nchars += az;
        }

        options.allow = s.join('|');

        regex = new RegExp(options.allow, 'gi');
        ch = (options.ichars + options.nchars).replace(regex, '');

        input.keypress(function (e) {
            var key = String.fromCharCode(!e.charCode ? e.which : e.charCode);

            if (ch.indexOf(key) != -1 && !e.ctrlKey) {
                e.preventDefault();
            }
        });

        input.blur(function () {
            var value = input.val(),
                j = 0;

            for (j; j < value.length; j++) {
                if (ch.indexOf(value[j]) != -1) {
                    //input.val('');
                	input.val(input.val().replace(value[j],""));
                    //return false;
                }
            }
            return false;
        });

        return input;
    },
    alphaNumericCustomEntry: function  (p) {
        return this.each(function () {
            $(this).alphaNumericEntry($.extend({ allow: ' ' }, p));
        });
    },
    emailAddressEntry: function  (p) {
        return this.each(function () {
            $(this).alphaNumericEntry($.extend({ allow: '@-._' }, p));
        });
    },
    currencyEntry: function  (p) {
        var az = 'abcdefghijklmnopqrstuvwxyz',
            aZ = az.toUpperCase();

        return this.each(function () {
            $(this).alphaNumericEntry($.extend({ nchars: az + aZ, allow: '.' }, p));
        });
    },
    numericEntry: function  (p) {
        var az = 'abcdefghijklmnopqrstuvwxyz',
            aZ = az.toUpperCase();

        return this.each(function () {
            $(this).alphaNumericEntry($.extend({ nchars: az + aZ }, p));
        });
    },
    alphaEntry: function  (p) {
        var nm = '1234567890';
        return this.each(function () {
            $(this).alphaNumericEntry($.extend({ nchars: nm }, p));
        });
    }
});