/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Database.Database;
import Model.AplikasiInventaris;
import View.ViewPetugasRemoveBarang;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 *
 * @author Riri
 */
public class ControllerPetugasRemoveBarang implements ActionListener {
    private ViewPetugasRemoveBarang view;
    private AplikasiInventaris app;
    Database db = new Database();
    String id;
    String nama;
    String jenis;
    int id_penyedia;
    int stock;
    String id_gudang;
    int slot;
    
    public ControllerPetugasRemoveBarang() {
        app = new AplikasiInventaris();
        view = new ViewPetugasRemoveBarang();
        view.setVisible(true);
        view.addListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        db.connect();
        Object source = e.getSource();
        if(source.equals(view.getBtnHapus())) {
            ResultSet rs = db.get("select * from daftarbarangpenyedia where idBarang = '" + view.getIDBarang() + "'");
            try {                
                if(rs.first()){
                    id = rs.getString("idbarang");
                    nama = rs.getString("namabarang");
                    jenis = rs.getString("jenisbarang");
                    id_penyedia = rs.getInt("id_penyedia");
                    stock = rs.getInt("stock");
                    rs.close();
                } else{
                    JOptionPane.showMessageDialog(null,"ID barang salah!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,"Gagal");
            }
            
            rs = db.get("SELECT * FROM daftargudang WHERE id_gudang = '" + view.getGudang() + "'");
            try {            
                if(rs.first()){
                    id_gudang = rs.getString("id_gudang");
                    slot = rs.getInt("slot");
                    rs.close();
                }else{
                    JOptionPane.showMessageDialog(null,"ID gudang salah!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,"Gagal!");
            }
            
            if(id!= null && id_gudang!=null){
                if(stock >= view.getJumlah()){
                    if(db.manipulate("INSERT INTO daftarbaranggudang "
                        + "(idBarang, namaBarang, jenisBarang, stock, id_penyedia, id_gudang)"
                        + " VALUES ('"
                        + id + "','"
                        + nama + "','"
                        + jenis + "',"
                        + view.getJumlah() + ","
                        + id_penyedia + ",'"
                        + id_gudang + "')") >= 1)
                    {
                        int sisa = (stock+view.getJumlah());
                        db.manipulate("UPDATE daftarbarangpenyedia SET stock = " 
                                + sisa + " WHERE daftarbarangpenyedia.idBarang = '"
                                + id + "';");
                        sisa = (slot+view.getJumlah());
                        db.manipulate("UPDATE daftargudang SET slot = " 
                                + sisa + " WHERE daftargudang.id_gudang = '"
                                + id_gudang + "';");
                        db.disconnect();
                        JOptionPane.showMessageDialog(null,"Barang berhasil dikeluarkan!");
                        view.reset();
                    } else{
                        JOptionPane.showMessageDialog(null,"Gagal mengeluarkan!");
                        view.reset();
                    }
                } else{
                    JOptionPane.showMessageDialog(null,"Stock tidak cukup!");
                    view.resetJum();
                }
            } else{
                view.reset();
            }
        } else if (source.equals(view.getBtnCancel())) {
            db.disconnect();
            view.reset();
            ControllerPetugasUtama pu = new ControllerPetugasUtama();
            view.dispose();
        }
    }
    
}
