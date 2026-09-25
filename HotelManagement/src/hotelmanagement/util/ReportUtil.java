package hotelmanagement.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;

public class ReportUtil {

    public static void showReport(String jrxmlPath) {
        try {
            InputStream reportStream = ReportUtil.class.getResourceAsStream(jrxmlPath);
            if (reportStream == null) {
                System.err.println("❌ Report file not found at: " + jrxmlPath);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                System.err.println("❌ DB connection is null.");
                return;
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, new HashMap<>(), conn
            );

            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            System.err.println("❌ Jasper error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void openBookingReport() {
        showReport("/jasperReports/booking_report.jrxml");
    }

    public static void main(String[] args) {
        openBookingReport();
    }
}