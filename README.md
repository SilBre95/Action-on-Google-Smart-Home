# Action-on-Google-Smart-Home
L’obiettivo del progetto è realizzare una applicazione Android con la quale poter collegare e gestire dispositivi connessi sfruttando la tecnologia “Action on Google Smart Home”. Questi dispositivi saranno controllabili non solo con l’app custom ma anche tramite l’Assistente di Google e l’app Google Home.

# Backend
Per poter realizzare questo progetto per prima cose è necessario creare una Smart Home Action nella Action Console (piattaforma per sviluppatori per estendere le capacità dell’Assistente di Google). Per poter gestire l’interazione con la smart home e con l’assistente di Google ho scelto di realizzare un servizio in Node.js che si interfaccia con Cloud Functions for Firebase e Firebase Realtime database. Questo servizio è composto da varie funzioni [(Backend/functions)](Backend/functions) :
- smarthome: attraverso la quale vengono gestite tutte gli smart home intents (sync, query, execute, disconect)
- fakeauth: per la gestione la richiesta di autorizzazione
- faketoken: per la gestione del token di autorizzazione
- login: per la gestione del login dell’utente associato alla smart home
- requestsync: per la comunicazione dello stato della smart home con Google Home Graph
- addMessage: per la comunicazione dei nuovi dispositivi che provengono dall’app
- reportstate: per riportare a Google Home Graph le modifiche apportate allo stato del dispositivo in seguito all’esecuzione di un comando

# Applicazione Android
L’applicazione [(GoogleAssistantProject)](GoogleAssistantProject) per la gestione di dispositivi connessi è stata pensata per poter offrire all’utente la possibilità di collegare alla smart home nuovi dispositivi, gestire i dispositivi connessi, scollegare i dispositivi e visualizzare lo stato corrente della propria smart home. L’app si presenta con una schermata principale in cui si può visualizzare la lista dei dispositivi collegati alla smart home. Per ogni dispositivo è presente un’icona, il nome del dispositivo e la tipologia. Con il pulsate “+” in basso sarà possibile accedere alla schermata utile per aggiungere un nuovo dispositivo. Per aggiungere un dispositivo è necessario inserire il nome, il modello, la ditta produttrice e la tipologia.
Per gestire i vari dispositivi collegati, basterà cliccare su uno di essi, presente nella lista, e si aprirà la schermata di gestione, in cui si potrà visualizzare lo stato attuale e modificarlo. Inoltre se si preme l’icona con il cestino si potrà anche eliminare il dispositivo.

# Alcune schermate dell'app
![alt text](https://github.com/SilBre95/Action-on-Google-Smart-Home/AppImages/photo5771453164489389847.jpg)
