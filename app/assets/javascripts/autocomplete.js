$(document).ready(function() {

    //======================================================
    // GOV.UK country lookup
    // https://alphagov.github.io/accessible-autocomplete/#progressive-enhancement
    //======================================================
    // auto complete country lookup, progressive enhancement
    // using version 2.5.7
    // need to invoke new enhanceSelectElement()
    //======================================================

    if(document.querySelectorAll('select[data-non-uk-countries]').length > 0) {
       accessibleAutocomplete.enhanceSelectElement({
            selectElement: document.querySelector("select[data-non-uk-countries]"),
            showAllValues: true,
            defaultValue: ''
        });
    }

     if(document.querySelectorAll('select[data-all-countries]').length > 0) {
       accessibleAutocomplete.enhanceSelectElement({
            selectElement: document.querySelector("select[data-all-countries]"),
            showAllValues: true,
            defaultValue: ''
        });
    }

    //======================================================
    // Assign aria-describedby to the dynamically created country input
    // Takes the aria-described by value from the select element and
    // allocates this to the created text input
    //======================================================
    if (document.querySelectorAll('select[data-all-countries]').length > 0) {
       var selectDescribedByValues = $('select[data-all-countries]').attr('aria-describedby');
       $(".autocomplete__wrapper #value").attr('aria-describedby', selectDescribedByValues);
    }
    if (document.querySelectorAll('select[data-non-uk-countries]').length > 0) {
       var selectDescribedByValues = $('select[data-non-uk-countries]').attr('aria-describedby');
       $(".autocomplete__wrapper #value").attr('aria-describedby', selectDescribedByValues);
    }


    //======================================================
    // Fix CSS styling of errors (red outline) around the country input dropdown
    //======================================================

    // Override autocomplete styles to apply correct error component design pattern
    if ($(".autocomplete-wrapper .error-message").length) $(".autocomplete__wrapper input").addClass('field-error');
    // Set the border colour to black with orange border when clicking into the input field
    $('.autocomplete__wrapper input').focus(function(e){
        if ($(".autocomplete-wrapper .error-message").length) $(".autocomplete__wrapper input").css({"border" : "4px solid #0b0c0c", "-webkit-box-shadow" : "none", "box-shadow" : "none"});
    })
    // Set the border colour back to red when clicking out of the input field
    $('.autocomplete__wrapper input').focusout(function(e){
        if ($(".autocomplete-wrapper .error-message").length) $(".autocomplete__wrapper input").css("border", "4px solid #d4351c");
    })


  //======================================================
  // Fix IE country lookup where clicks are not registered when clicking list items
  //======================================================

    // temporary fix for IE not registering clicks on the text of the results list for the country autocomplete
    $('body').on('mouseup', ".autocomplete__option > strong", function(e){
        e.preventDefault(); $(this).parent().trigger('click');
    })
    // temporary fix for the autocomplete holding onto the last matching country when a user then enters an invalid or blank country
    $('input[role="combobox"]').on('keydown', function(e){
        if (e.which != 13 && e.which != 9) {
             var sel = document.querySelector('.autocomplete-wrapper select');
             sel.value = "";
        }
    })

});