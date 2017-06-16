# frontend
Barebones event sourcing with CQRS to understand the concepts

Based on a talk given by Sebastian von Conrad at SATURN 2017 (https://www.youtube.com/watch?v=EqpalkqJD8M)

This implementation imagines a system where Buyers purchase from Offers of Products from Markets. It is all driven
from a very simple frontend using Akka for Actors, but otherwise pretty vanilla.

The CommandHandler takes commands from the FrontEnd, passes them to various domain (aggregate) command handlers which
attempt to process them; if the commands are successful, they result in events posted to the EventStore. The EventStore
maintains all events forever.

The Projectors are simple examples of targeted query stores which track particular aspects of events that have occurred.

The one Reactor is an example of events feeding other events; in this case, Buyers' purchases automatically updating their
statuses.

Run it by 'gradle run'
You can process some typical commands by running the command "ProcessFile", which runs a set of commands from the text file
"commands.txt".
