package ua.kpi.its.lab.security

import jakarta.servlet.DispatcherType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.ee10.servlet.FilterHolder
import org.eclipse.jetty.ee10.servlet.ServletContextHandler
import org.eclipse.jetty.ee10.servlet.ServletHolder
import org.eclipse.jetty.server.Server
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.servlet.DispatcherServlet
import java.util.*

/**
 * ***********************************************************************
 * ***********************************************************************
 * ************************ NO NEED TO EDIT ******************************
 * ***********************************************************************
 * ***********************************************************************
 */

/**
 * The main entry point of the application that sets up and starts a Jetty server with Spring MVC configured.
 * It demonstrates how to programmatically configure a Jetty server to use Spring's WebApplicationContext and
 * DispatcherServlet for handling web requests.
 */
fun main(args: Array<String>) {
    // Log the initiation of the server startup process.
    logger.info("Starting server at port {}", 8080)

    // Configure and initialize the Jetty server on port 8080.
    val server = Server(8080).apply {
        handler = servletContextHandler // Set a custom handler to integrate Spring MVC.
        addRuntimeShutdownHook(logger) // Add a shutdown hook for clean server shutdown.
    }

    // Start the server.
    server.start()
    logger.info("Server started at port {}", 8080) // Log the successful server start.

    // Wait for the server to be terminated before exiting the main thread.
    server.join()
}

/**
 * A logger instance for logging information about the server's operation.
 */
private val logger: Logger
    get() = LogManager.getLogger()

/**
 * Configures the servlet context handler for the Jetty server to use Spring's [DispatcherServlet].
 * This handler integrates Spring MVC into the Jetty server, enabling the handling of web requests
 * through Spring's web framework.
 *
 * @return The configured [ServletContextHandler].
 */
private val servletContextHandler: ServletContextHandler
    get() {
        val webAppContext = webApplicationContext // Obtain the Spring web application context.
        val dispatcherServlet = DispatcherServlet(webAppContext) // Create Spring's DispatcherServlet.
        val filter = DelegatingFilterProxy("springSecurityFilterChain", webAppContext) // Create the DelegatingFilterProxy to use as Spring Security filter chain.
        val springServletHolder = ServletHolder("dispatcherServlet", dispatcherServlet) // Wrap the servlet in a holder.

        // Configure the ServletContextHandler.
        return ServletContextHandler(ServletContextHandler.SESSIONS).apply {
            errorHandler = null // Optional: Custom error handling can be configured here.
            contextPath = "/" // Set the context path to the root.
            addServlet(springServletHolder, "/*") // Register the DispatcherServlet to handle all requests.
            addFilter(FilterHolder(filter), "/*", EnumSet.of(DispatcherType.REQUEST)) // Register the Spring Security filter chain to handle all requests.
            addEventListener(ContextLoaderListener(webAppContext)) // Ensure the Spring context is loaded and closed correctly.
        }
    }

/**
 * Creates and configures the Spring [WebApplicationContext] programmatically.
 * This context is used to configure Spring MVC through annotations.
 *
 * @return The configured [AnnotationConfigWebApplicationContext].
 */
private val webApplicationContext: WebApplicationContext
    get() = AnnotationConfigWebApplicationContext().apply {
        setConfigLocation("ua.kpi.its.lab.security.config") // Specify the package where the configuration class(es) are located.
    }

/**
 * Adds a runtime shutdown hook to the server for a graceful shutdown.
 * This method attempts to stop the server when the JVM is shutting down.
 *
 * @param logger A logger instance for logging shutdown events or errors.
 */
private fun Server.addRuntimeShutdownHook(logger: Logger) {
    Runtime.getRuntime().addShutdownHook(Thread {
        if (isStarted) {
            stopAtShutdown = true
            try {
                // Attempt to stop the server on JVM shutdown.
                stop()
            } catch (e: Exception) {
                logger.error("Error while stopping jetty server: ${e.message}", e)
            }
        }
    })
}