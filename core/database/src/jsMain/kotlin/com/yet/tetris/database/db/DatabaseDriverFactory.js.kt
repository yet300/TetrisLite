package com.yet.tetris.database.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DatabaseDriverFactory {
    actual suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver =
        WebWorkerDriver(
            Worker(
                scriptURL = resolveWorkerScriptUrl(),
            ),
        ).also { schema.create(it).await() }

    private fun resolveWorkerScriptUrl(): String =
        js(
            """(function() {
          var scripts = document.scripts;
          var i;
          for (i = 0; i < scripts.length; i++) {
            var src = scripts[i].src || "";
            if (/\/webApp\.js(?:[?#].*)?$/.test(src)) {
              return new URL("sqlite.worker.js", new URL("./", src)).toString();
            }
          }

          var baseElement = document.querySelector("base");
          var baseHref = baseElement ? baseElement.getAttribute("href") : null;
          if (baseHref) {
            return new URL("sqlite.worker.js", new URL(baseHref, window.location.origin + "/")).toString();
          }

          var pathSegments = window.location.pathname.split("/").filter(function(segment) {
            return segment.length > 0;
          });
          var githubPagesBase =
            window.location.hostname.indexOf(".github.io") >= 0 && pathSegments.length > 0
              ? "/" + pathSegments[0] + "/"
              : "/";

          return new URL(githubPagesBase + "sqlite.worker.js", window.location.origin).toString();
        })()""",
        ) as String
}
