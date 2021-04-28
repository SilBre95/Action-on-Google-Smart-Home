# Action-on-Google-Smart-Home
L’obiettivo del progetto è realizzare una applicazione Android con la quale poter collegare e gestire dispositivi connessi sfruttando la tecnologia “Action on Google Smart Home”. Questi dispositivi saranno controllabili non solo con l’app custom ma anche tramite l’Assistente di Google e l’app Google Home.

Per poter realizzare questo progetto per prima cose è necessario creare una Smart Home Action nella Action Console (piattaforma per sviluppatori per estendere le capacità dell’Assistente di Google). Per poter gestire l’interazione con la smart home e con l’assistente di Google ho scelto di realizzare un servizio in Node.js che si interfaccia con Cloud Functions for Firebase e Firebase Realtime database. Questo servizio è composto da varie funzioni [SilBre95/Action-on-Google-Smart-Home/Backend/functions]:
- smarthome: attraverso la quale vengono gestite tutte gli smart home intents (sync, query, execute, disconect)
- fakeauth: per la gestione la richiesta di autorizzazione
- faketoken: per la gestione del token di autorizzazione
- login: per la gestione del login dell’utente associato alla smart home
- requestsync: per la comunicazione dello stato della smart home con Google Home Graph
- addMessage: per la comunicazione dei nuovi dispositivi che provengono dall’app
- reportstate: per riportare a Google Home Graph le modifiche apportate allo stato del dispositivo in seguito all’esecuzione di un comando

