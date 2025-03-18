package com.example.bookshopwebapplication.utils;

import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiPart<T> {
    private final ServletFileUpload upload = new ServletFileUpload();
    private final FileItemFactory factory = new DiskFileItemFactory();
    private FileItem fileItem = null;
    private T object = null;
    private final Gson gson = new Gson();

    public static <T> T get(ServletRequest request, Class<T> objectClass) throws IOException {
        return new MultiPart<T>().getMultiPart(request, objectClass);
    }

    private T getMultiPart(ServletRequest request, Class<T> objectClass) {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
            if (!isMultipart) {
                return null;
            }
            // Tạo factory để lưu file tạm
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Cấu hình factory
            ServletContext servletContext = httpServletRequest.getServletContext();
            File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            factory.setRepository(repository);

            // Tạo file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Thiết lập encoding UTF-8 để hỗ trợ tiếng Việt
            upload.setHeaderEncoding("UTF-8");
            upload.setFileItemFactory(factory);

            // Parse request để lấy các field
            // Parse request để lấy các field
            List<FileItem> items = upload.parseRequest(httpServletRequest);
            // Tạo map để lưu các giá trị form
            Map<String, String> formFields = new HashMap<>();
            String imageName = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    formFields.put(item.getFieldName(), item.getString("UTF-8"));
                } else {
                    // Xử lý trường file upload
                    String fieldName = item.getFieldName();
                    if ("image".equals(fieldName) && item.getSize() > 0) {
                        // Lấy tên file gốc
                        String fileName = new File(item.getName()).getName();

                        // Tạo tên file duy nhất để tránh trùng lặp
                        imageName = System.currentTimeMillis() + "_" + fileName;

                        // Tạo đường dẫn lưu file
                        String uploadPath = servletContext.getRealPath("") + File.separator + "UPLOAD_DIRECTORY";
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdir();
                        }

                        // Lưu file vào thư mục
                        File uploadedFile = new File(uploadPath + imageName);
                        item.write(uploadedFile);

                        System.out.println("File đã được upload: " + uploadedFile.getAbsolutePath());

                        // Lưu tên file vào map
                        formFields.put(fieldName, uploadedFile.getAbsolutePath());
                    }
                }
            }

            // Xử lý dữ liệu từ form
            object = gson.fromJson(gson.toJson(formFields), objectClass);

            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
