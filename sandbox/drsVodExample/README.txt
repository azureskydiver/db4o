
**************************************************************
                      drsVodExample
**************************************************************

All Java classes in the drs.vod.example package contain
main() methods that can be run.

Before you can get started with running these classes you need
to prepare the Vod Database for events and you have to start
the EventDriver and the EventProcessor applications.
 
 
To do this: 

- Adjust the script /scripts/setEnvironment to the paths on 
your machine

- Create the sample database using /scripts/createDatabase

- Create the event schema using /scripts/createEventSchema

- Start the event driver using /scripts/startEventDriver
It should come up in a separate console stay up and print:
'Event Daemon : Starting the event daemon for dRSVodExample'

- Start the event processor using /scripts/startEventProcessor
It should come up in a separate console stay up and print:
'VOD EventProcessor for dRS is listening for events.'

**************************************************************
                       * Important *
**************************************************************
If you want to use dRS replication in productive use, both the
EventDriver and the EventProcessor always need to run against
the VOD database to track changes.
 **************************************************************
 
Once you have done the above you can play with all the classes
in the drs.vod.example package to store, replicate and print
the content of a VOD and a db4o database.
