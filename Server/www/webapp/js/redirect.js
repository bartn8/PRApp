var getRedirectURL = () => {
    //Funzione redirect: se sono passato da una pagina perché non loggato allora ritorno li
    let url_string = window.location.href;
    //Recupero dati GET.
    let url = new URL(url_string);
    let redirect = decodeURIComponent(url.searchParams.get("redirect"));
    //Faccio il redirect con a default index.html
    redirect = redirect !== 'null' ? redirect : "index.html";
    return redirect;
}

var redirect = (url) => {
    console.log("Passo a:"+url);
    window.location.href = url;
};

var passRedirect = (page, param) => {
    let finalURL = page;
    if(param !== undefined){
        finalURL = finalURL + "?redirect=" +param;
    }
    redirect(finalURL);
};