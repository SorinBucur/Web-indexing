# Web-indexing
Web indexing with replicated workers and map reduce

Web indexing este folosit pentru a facilita obtinerea rapida si precisa a informatiilor existente in paginile Web de catre motoarele de cautare care colecteaza, parseaza si stocheaza datele in avans.

Am implementat un program paralel in Java pentru indexarea unui set de documente text primit ca input si apoi am verificat daca exista documente in set cu un grad de similaritate intre ele mai mare decat o valoare limita.

In urma procesului de indexare se determina numarul de aparitii al fiecarui cuvânt existent intr-un document, obtinându-se o lista de perechi (cuvânt, numar de apariţii). Programul calculeareaza similaritatatea semantica (sub forma de procent) intre toate documentele indexate si sa afiseaza documentele cu grad maxim de similaritate.
