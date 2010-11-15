
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
- Start the event processor using /scripts/startEventProcessor


**************************************************************
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                        Important
                        
If you want to use dRS replication in productive use, both the
EventDriver and the EventProcessor always need to run against
the VOD database you are working against, so changes to objects
can be tracked and corresponding ObjectInfo instances can be
created.

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 **************************************************************

