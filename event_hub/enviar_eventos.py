import asyncio

from azure.eventhub import EventData
from azure.eventhub.aio import EventHubProducerClient

#EVENT_HUB_CONNECTION_STR = "Endpoint=sb://meuhackcaixa.servicebus.windows.net/;SharedAccessKeyName=minha-politica;SharedAccessKey=kpOB1ZLepFAnb3evFzjspxCXX+voi6Inn+AEhHZBSlA=;EntityPath=testehackcaixa"
EVENT_HUB_CONNECTION_STR = "Endpoint=sb://eventhack.servicebus.windows.net/;SharedAccessKeyName=hack;SharedAccessKey=HeHeVaVqyVkntO2FnjQcs2Ilh/4MUDo4y+AEhKp8z+g=;EntityPath=simulacoes"


async def run():
    # Create a producer client to send messages to the event hub.
    # Specify a connection string to your event hubs namespace and
    # the event hub name.
    producer = EventHubProducerClient.from_connection_string(
        conn_str=EVENT_HUB_CONNECTION_STR
    )
    async with producer:
        # Create a batch.
        event_data_batch = await producer.create_batch()

        # Add events to the batch.
        event_data_batch.add(EventData("First event "))
        event_data_batch.add(EventData("Second event"))
        event_data_batch.add(EventData("Third event"))

        # Send the batch of events to the event hub.
        await producer.send_batch(event_data_batch)

asyncio.run(run())    