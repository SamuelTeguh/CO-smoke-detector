# CO-smoke-detector

## Perancangan Sistem

Perancangan Sistem dijelaskan pada Gambar. Nomor 1 menunjukan saat ada orang yang sedang merokok dalam ruangan, sensor akan mendeteksi. Nomor 2 menunjukan  sensor MQ-2 mengirimkan output kepada NodeMCU, lalu NodeMCU akan mengirimkan perintah kepada kamera ArduCAM OV2640 untuk mengambil foto ruangan, ditunjukan pada nomor 3 dan pengambilan gambar ditunjukan pada nomor 4. Nomor 5 menunjukan kamera akan mengirimkan gambar kepada NodeMCU dan akan diunggah kedalam webserver yang ada pada NodeMCU. Nomor 6 menunjukan NodeMCU mengirim data deteksi ke dalam cloud database. Nomor 7 menunjukan cloud database mengirimkan notifikasi kepada aplikasi yang ada pada smartphone berisikan data deteksi.

<p align="center"><img src="https://i.ibb.co/jhsP1vy/asd.png"/></p>

## Perangkaian Alat

Alat pendeteksi asap rokok tersusun dari NodeMCU, sensor gas MQ-2 dan sensor kamera ArduCAM Mini OV2640 2MP. Sensor gas MQ-2 disambungkan ke NodeMCU yang berfungsi sebagai papan pengembangan agar sensor dapat mendeteksi karbon monoksida dan asap dalam satuan ppm. Sensor kamera ArduCAM Mini OV2640 2MP juga disambungkan ke NodeMCU yang berfungsi untuk mengambil foto saat terdeteksinya ada asap rokok dalam sebuah ruangan.

<p align="center"><img src="https://i.ibb.co/whTHjg2/asd1.png"/></p>

## Perancangan Aplikasi Mobile

Aplikasi mobile akan terkoneksi dengan cloud database untuk mengambil URL foto yang tertangkap akan ditamplikan pada halaman penampilan foto dan juga untuk mengubah konsentrasi gas yang terdeteksi yang akan ditampilkan pada halaman konfigurasi sensor. Button 1 berfungsi untuk mengaktifkan kembali sensor gas, Button 2 berfungsi untuk mengubah konsentrasi gas yang terdeteksi dan TextBox1/TextBox2 untuk menginput konsenstrasi gas yang diinginkan untuk dideteksi dalam satuan ppm.

<p align="center"><img src="https://i.ibb.co/ZN2y5k6/asd2.png"/></p>

Pemograman aplikasi mobile dilakukan menggunakan Android Studio. Untuk membuat koneksi antara aplikasi dengan cloud database dibutuhkan file JSON dari Firebase untuk diletakan dalam gradle aplikasi pada Android Studio. Aplikasi mobile akan dapat mengambil gambar dari cloud database dan mengubah data batas sensor yang ada pada database. Gambar akan ditampilkan dalam aplikasi mobile tersebut. Aplikasi ini memiliki minimum API level 23 / Android Versi 6.0 dan membutuhkan izin read phone state, access network state, internet, write dan read external storage.

## Hasil Pengujian Dalam Ruangan

Pengujian alat pendeteksi asap rokok dilakukan pada ruangan berukuran 3,5m x 4m x 3m dengan kriteria ruangan suhu sekitar 25oC – 35oC dan kelembapan udara sekitar 80%RH-95%RH dan dalam ruangan yang bebas dari sumber asap lainnya seperti asap dapur dan asap kendaraan serta ruangan yang menyediakan koneksi internet. Sesuai dengan hasil pengambilan data, alat diletakan diatas sumber asap rokok. Alat berhasil mengambil foto dan mengirimkan foto tersebut ke dalam aplikasi mobile dengan hasil seperti pada Gambar. Nilai 05-2-2019 Menunjukan tanggal, bulan dan tahun saat terdeteksinya asap rokok. CO Value dan Smoke Value merupakan konsentrasi gas yang terdeteksi. Time merupakan waktu saat diambilnya gambar tersebut. Semua data-data tersebut didapatkan dari cloud database.

<p align="center"><img src="https://i.ibb.co/tmZdZYj/asd3.png"/></p>

## Referensi

https://github.com/dmainmon/ArduCAM-mini-ESP8266-12E-Camera-Server/blob/master/ArduCam_ESP8266_FileCapture.ino
