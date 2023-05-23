package de.p10r

import org.http4k.events.Event
import org.http4k.events.MetadataEvent
import org.http4k.tracing.Actor
import org.http4k.tracing.ActorResolver
import org.http4k.tracing.ActorType
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.Tracer
import org.http4k.tracing.junit.ReportingMode
import org.http4k.tracing.junit.TracerBulletEvents
import org.http4k.tracing.persistence.FileSystem
import org.http4k.tracing.renderer.PumlInteractionDiagram
import org.http4k.tracing.renderer.PumlSequenceDiagram
import org.http4k.tracing.tracer.HttpTracer
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

abstract class RecordTraces {
    @RegisterExtension
    val events = TracerBulletEvents(
        listOf(::HttpTracer, ::DbTracer).map { tracer: (ActorResolver) -> Tracer -> tracer(actorByService()) },
        listOf(PumlSequenceDiagram, PumlInteractionDiagram),
        TraceRenderPersistence.FileSystem(File(".generated")),
        reportingMode = ReportingMode.Always
    )
}

private fun actorByService(): (MetadataEvent) -> Actor = ActorResolver {
    Actor(it.metadata["service"]!!.toString(), ActorType.System)
}

private fun DbTracer(actorResolver: ActorResolver) = Tracer { parent, _, _ ->
    parent
        .takeIf { metadataEvent -> metadataEvent.event is DbCall }
        ?.let { metadataEvent -> dbTraceOf(actorResolver, metadataEvent) }
        ?.let { trace -> listOf(trace) }
        ?: emptyList()
}

private fun dbTraceOf(actorResolver: ActorResolver, metadataEvent: MetadataEvent) =
    BiDirectional(
        origin = actorResolver(metadataEvent),
        target = Actor("db", ActorType.Database),
        request = (metadataEvent.event as DbCall).name,
        children = emptyList()
    )

data class DbCall(val name: String) : Event
