package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String[] parts = request.getRequestURI().split("\\+", -1);
        File[] files = new File[parts.length];

        File srcDir = new File("src/main/webapp/static").getCanonicalFile();
        File defaultDir = new File(getServletContext().getRealPath("/static")).getCanonicalFile();

        for (int i = 0; i < parts.length; i++) {
            String path = parts[i];

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            if (path.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            File fromSrc = new File(srcDir, path).getCanonicalFile();
            File fromDefault = new File(defaultDir, path).getCanonicalFile();

            if (!fromSrc.toPath().startsWith(srcDir.toPath())
                    || !fromDefault.toPath().startsWith(defaultDir.toPath())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            files[i] = fromSrc.isFile() ? fromSrc : fromDefault;

            if (!files[i].isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        response.setContentType(getServletContext().getMimeType(files[0].getName()));

        try (OutputStream outputStream  = response.getOutputStream()) {
            for (File file : files) {
                Files.copy(file.toPath(), outputStream );
            }
        }

    }
}
