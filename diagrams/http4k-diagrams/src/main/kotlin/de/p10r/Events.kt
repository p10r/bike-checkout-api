package de.p10r

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.EventFilters
import org.http4k.events.Events
import org.http4k.events.HttpEvent
import org.http4k.events.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters

fun AppEvents(name: String, base: Events) = EventFilters.AddZipkinTraces()
    .then(EventFilters.AddServiceName(name))
    .then(base)

fun OutgoingHttp(events: Events, http: HttpHandler) =
    ClientFilters.RequestTracing()
        .then(ResponseFilters.ReportHttpTransaction { events(HttpEvent.Outgoing(it)) })
        .then(http)

fun IncomingHttp(events: Events, http: HttpHandler) =
    ServerFilters.RequestTracing()
        .then(ResponseFilters.ReportHttpTransaction { events(HttpEvent.Incoming(it)) })
        .then(http)
