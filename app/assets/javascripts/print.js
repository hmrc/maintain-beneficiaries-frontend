$(document).ready(function() {

     function beforePrintCall(){
            if($('.no-details').length > 0){
                // store current focussed element to return focus to later
                var fe = document.activeElement;
                // store scroll position
                var scrollPos = window.pageYOffset;
                $('details').not('.open').each(function(){
                    $(this).find('summary').trigger('click');
                });
                // blur focus off current element in case original cannot take focus back
                $(document.activeElement).blur();
                // return focus if possible
                $(fe).focus();
                // return to scroll pos
                window.scrollTo(0,scrollPos);
            } else {
                $('details').attr("open","open");
            }
    }

    function afterPrintCall(){
            if($('.no-details').length > 0){
                // store current focussed element to return focus to later
                var fe = document.activeElement;
                // store scroll position
                var scrollPos = window.pageYOffset;
                $('details').each(function(){
                    $(this).find('summary').trigger('click');
                });
                // blur focus off current element in case original cannot take focus back
                $(document.activeElement).blur();
                // return focus if possible
                $(fe).focus();
                // return to scroll pos
                window.scrollTo(0,scrollPos);
            } else {
                $('details').removeAttr("open");
            }
        }

        //Chrome
        if(typeof window.matchMedia != 'undefined'){
            mediaQueryList = window.matchMedia('print');
            mediaQueryList.addListener(function(mql) {
                if (mql.matches) {
                    beforePrintCall();
                };
                if (!mql.matches) {
                    afterPrintCall();
                };
            });
        }

        //Firefox and IE (above does not work)
        window.onbeforeprint = function(){
            beforePrintCall();
        }
        window.onafterprint = function(){
            afterPrintCall();
    }

});