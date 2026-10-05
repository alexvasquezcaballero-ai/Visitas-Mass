package cl.mass.visitas.data

data class StoreAssignment(val salesLead:String,val supervisor:String,val store:String)

object StoreDirectory {
    private val csv = """Alejandro Vasquez,Manuel Buitron,1268 - Moche8 TRU MS
Alejandro Vasquez,Manuel Buitron,1470 - Grau 1 TRU MS
Alejandro Vasquez,Manuel Buitron,1474 - Wichan 30 TRU MS
Alejandro Vasquez,Manuel Buitron,1851 - Milagro5 TRU MS
Alejandro Vasquez,Manuel Buitron,1862 - Grau54 TRU MS
Alejandro Vasquez,Manuel Buitron,2753 - Calle45 Tru Ms
Alejandro Vasquez,Manuel Buitron,2822 - Milagro8 Tru Ms
Alejandro Vasquez,Castañeda Marvin,1277 - JesusX1 TRU MS
Alejandro Vasquez,Castañeda Marvin,1336 - America9 TRU MS
Alejandro Vasquez,Castañeda Marvin,1421 - Juan 5 TRU MS
Alejandro Vasquez,Castañeda Marvin,1471 - Larco 6 TRU MS
Alejandro Vasquez,Castañeda Marvin,1782 - JuanM5 TRU MS
Alejandro Vasquez,Castañeda Marvin,2105 - Roma5 TRU MS
Alejandro Vasquez,Gomez Deysi,1300 - Jerusa4 TRU MS
Alejandro Vasquez,Gomez Deysi,1458 - Cahuid Q42 TRU MS
Alejandro Vasquez,Gomez Deysi,1610 - Indoame 5 TRU MS
Alejandro Vasquez,Gomez Deysi,2163 - Manuel2 TRU MS
Alejandro Vasquez,Gomez Deysi,2254 - 22Feb26 Tru Ms
Alejandro Vasquez,Gomez Deysi,2398 - Arevalo3 Tru Ms
Alejandro Vasquez,Freddy Lavado,1272 - Casale6 TRU MS
Alejandro Vasquez,Freddy Lavado,1306 - Metropoa18 TRU MS
Alejandro Vasquez,Freddy Lavado,1376 - Casal3 TRU MS
Alejandro Vasquez,Freddy Lavado,1578 - Metropol 1 TRU MS
Alejandro Vasquez,Freddy Lavado,2068 - Teodoro4 TRU MS
Alejandro Vasquez,Freddy Lavado,2100 - Chanchan3 TRU MS
Alejandro Vasquez,Alejandra Roncal,1373 - 22FEB7 TRU MS
Alejandro Vasquez,Alejandra Roncal,1461 - Condor 13 TRU MS
Alejandro Vasquez,Alejandra Roncal,1865 - Egipto6 TRU MS
Alejandro Vasquez,Alejandra Roncal,2010 - Blanco22 TRU MS
Alejandro Vasquez,Alejandra Roncal,2086 - Marti15 TRU MS
Alejandro Vasquez,Alejandra Roncal,2735 - Mateo9 Tru Ms
Alejandro Vasquez,Alejandra Roncal,2860 - Pettion4 Tru Ms
Alejandro Vasquez,Prentice Daniel,1389 - 26 Marzo 15 TRU MS
Alejandro Vasquez,Prentice Daniel,1472 - Octubre 7 TRU MS
Alejandro Vasquez,Prentice Daniel,1715 - 5nov7 TRU MS
Alejandro Vasquez,Prentice Daniel,1774 - Sauces8 TRU MS
Alejandro Vasquez,Prentice Daniel,2689 - Barrio3B Tru Ms
Alejandro Vasquez,Prentice Daniel,2754 - 26Marzo3 Tru Ms
Alejandro Vasquez,Jhon Sanchez,1791 - Paname9 TRU MS
Alejandro Vasquez,Jhon Sanchez,1902 - Celso8 TRU MS
Alejandro Vasquez,Jhon Sanchez,1918 - Paname47 TRU MS
Alejandro Vasquez,Jhon Sanchez,1984 - Mayo4 TRU MS
Alejandro Vasquez,Jhon Sanchez,2070 - Real4 TRU MS
Alejandro Vasquez,Jhon Sanchez,2401 - Manco9 Tru Ms
Alejandro Vasquez,Cachay Ana,2230 - Bolivar26 TRU MS
Alejandro Vasquez,Cachay Ana,2244 - Ancash4 Tru Ms
Alejandro Vasquez,Cachay Ana,2410 - Mariscal1 Tru Ms
Alejandro Vasquez,Cachay Ana,2453 - Olaya37 Tru Ms
Alejandro Vasquez,Cachay Ana,2632 - Cajamarca755 Tru Ms
Manuel Buitron,Aguilar Nilver,1302 - Larco2 TRU MS
Manuel Buitron,Aguilar Nilver,1486 - Paname A1 TRU MS
Manuel Buitron,Aguilar Nilver,1824 - Manco6 TRU MS
Manuel Buitron,Aguilar Nilver,1903 - Larco 764 TRU MS
Manuel Buitron,Aguilar Nilver,2538 - Pablo5 Tru Ms
Manuel Buitron,Aguilar Nilver,2659 - Bolivia6 Tru Ms
Manuel Buitron,Campana Manuel,1623 - Alameda 2 TRU MS
Manuel Buitron,Campana Manuel,1698 - Paname4 TRU MS
Manuel Buitron,Campana Manuel,1730 - Viru20 TRU MS
Manuel Buitron,Campana Manuel,1745 - Victor10 TRU MS
Manuel Buitron,Campana Manuel,1747 - Garcia16 TRU MS
Manuel Buitron,Campana Manuel,1773 - Victor18 TRU MS
Manuel Buitron,Campana Manuel,1881 - JoseI1 TRU MS
Manuel Buitron,José Chirinos,1313 - Marina5 TRU MS
Manuel Buitron,José Chirinos,1598 - Bologne 4 TRU MS
Manuel Buitron,José Chirinos,1798 - Pedro8 TRU MS
Manuel Buitron,José Chirinos,2017 - Rivera5 TRU MS
Manuel Buitron,José Chirinos,2199 - Jose5 TRU MS
Manuel Buitron,José Chirinos,2346 - Indec18 Tru Ms?
Manuel Buitron,Eduardo Cabello,1362 - Seoane11 TRU MS
Manuel Buitron,Eduardo Cabello,1381 - Huaman2 TRU MS
Manuel Buitron,Eduardo Cabello,1473 - Valle C57 TRU MS
Manuel Buitron,Eduardo Cabello,1706 - Golf C24 TRU MS
Manuel Buitron,Eduardo Cabello,1759 - Hayar3 TRU MS
Manuel Buitron,Eduardo Cabello,1827 - Larco11 TRU MS
Manuel Buitron,Keren Marquez,1355 - Borja2 TRU MS
Manuel Buitron,Keren Marquez,1372 - Clara1 TRU MS
Manuel Buitron,Keren Marquez,1411 - ValleE3 TRU MS
Manuel Buitron,Keren Marquez,1457 - Angeles 3 TRU MS
Manuel Buitron,Keren Marquez,1621 - Junin A7 TRU MS
Manuel Buitron,Keren Marquez,2882 - Perlae1 Tru Ms
Manuel Buitron,Gamboa Franco,1264 - Palma8 TRU MS
Manuel Buitron,Gamboa Franco,1405 - Reforma 13 TRU MS
Manuel Buitron,Gamboa Franco,1413 - Miguel 2 TRU MS
Manuel Buitron,Gamboa Franco,1614 - Merced 3 TRU MS
Manuel Buitron,Gamboa Franco,1933 - Castro6 TRU MS
Manuel Buitron,Gamboa Franco,2082 - Contador7 TRU MS
Manuel Buitron,Brayan Lozano,1356 - Bolivar5 TRU MS
Manuel Buitron,Brayan Lozano,1386 - 29 Dic 4 TRU MS
Manuel Buitron,Brayan Lozano,1447 - Incas 4ï¿½Â TRU MS
Manuel Buitron,Brayan Lozano,1615 - Prada O13 TRU MS
Manuel Buitron,Brayan Lozano,1997 - Diego7 TRU MS
Manuel Buitron,Brayan Lozano,2171 - España24 TRU MS
Edwin Casanova,Melendez Carlos,1770 - Progre3 TRU MS
Edwin Casanova,Melendez Carlos,1786 - Grau9 TRU MS
Edwin Casanova,Melendez Carlos,1870 - Romac5 TRU MS
Edwin Casanova,Melendez Carlos,2174 - Junin82 TRU MS
Edwin Casanova,Melendez Carlos,2622 - Progre205 Tru Ms
Edwin Casanova,Melendez Carlos,2713 - Grande40 Tru Ms
Edwin Casanova,Castillo Jefferson,1276 - Carrion10TRU MS
Edwin Casanova,Castillo Jefferson,1299 - Grau14 TRU MSÂ
Edwin Casanova,Castillo Jefferson,1901 - Bejar6 TRU MS
Edwin Casanova,Castillo Jefferson,1925 - Inca12 TRU MS
Edwin Casanova,Castillo Jefferson,2284 - Carrion19 Tru Ms
Edwin Casanova,Castillo Jefferson,2451 - Victor12 Tru Ms
Edwin Casanova,Lorena Cotrina,1280 - Pinos3 TRU MS
Edwin Casanova,Lorena Cotrina,1723 - RamonG1 TRU MS
Edwin Casanova,Lorena Cotrina,1748 - Mateo2 TRU MS
Edwin Casanova,Lorena Cotrina,2063 - Dean2 TRU MS
Edwin Casanova,Lorena Cotrina,2599 - Rivera504 Tru Ms
Edwin Casanova,Lorena Cotrina,2777 - Carlosj1 Tru Ms
Edwin Casanova,Charcape Eduardo,1293 - Ejerci999TRU MS
Edwin Casanova,Charcape Eduardo,1340 - UCEDAA9 TRU MS
Edwin Casanova,Charcape Eduardo,1616 - Vallejo 4 TRU MS
Edwin Casanova,Charcape Eduardo,1718 - Ejerci3 TRU MS
Edwin Casanova,Charcape Eduardo,2396 - Salva6 Tru Ms
Edwin Casanova,Charcape Eduardo,2690 - Tacna3 Tru Ms
Edwin Casanova,Brandon Chimbor,1266 - Carrion1 TRU MS
Edwin Casanova,Brandon Chimbor,1455 - Zafiros 2 TRU MS
Edwin Casanova,Brandon Chimbor,1834 - Pesque36 TRU MS
Edwin Casanova,Brandon Chimbor,1842 - Maria6 TRU MS
Edwin Casanova,Brandon Chimbor,2216 - Santa21 TRU MS
Edwin Casanova,Brandon Chimbor,2248 - Capac24 Tru
Edwin Casanova,Espinoza Anapaula,1304 - Vallejo10 TRU MSÂ
Edwin Casanova,Espinoza Anapaula,1363 - Unanue 5 TRU MS
Edwin Casanova,Espinoza Anapaula,1609 - Santa 13 TRU MS
Edwin Casanova,Espinoza Anapaula,1989 - Gasset2TRU MS
Edwin Casanova,Espinoza Anapaula,2237 - Camino16 Tru Ms
Edwin Casanova,Espinoza Anapaula,2478 - Cesarv13 Tru Ms
Edwin Casanova,Melo Jonathan,1263 - Buenos3 TRU MS
Edwin Casanova,Melo Jonathan,1265 - Mansi16 TRU MS
Edwin Casanova,Melo Jonathan,1318 - Alcides4 TRU MSÂ
Edwin Casanova,Melo Jonathan,1374 - AMERICA23 TRU MS
Edwin Casanova,Melo Jonathan,1408 - Esmeral 3 TRU MS
Edwin Casanova,Melo Jonathan,1583 - Tupac 6 TRU MS
Dany Lozano,Ana Vargas,1914 - SanPedroG2 CHB MS
Dany Lozano,Ana Vargas,2080 - Carlos14 CHB MS
Dany Lozano,Ana Vargas,2135 - Marañon4 CHB MS
Dany Lozano,Ana Vargas,2292 - Jorge5 Chb Ms
Dany Lozano,Ana Vargas,2551 - Delmar4 Chb Ms
Dany Lozano,Ana Vargas,2125 - Panam4 CHB MS
Dany Lozano,Dany Lozano,2318 - Pallasca06 Chb Ms
Dany Lozano,Dany Lozano,2630 - Libertad1 Chb Ms
Dany Lozano,Dany Lozano,1847 - Bella CHB MS
Dany Lozano,Dany Lozano,2083 - Agraria22 CHB MS
Dany Lozano,Dany Lozano,2375 - Pacif6 CHB MS
Dany Lozano,Franco Garcia,2101 - VillaE1 CHB MS
Dany Lozano,Franco Garcia,1938 - AlamosA1 CHB MS
Dany Lozano,Franco Garcia,2749 - Bellan14 Chb Ms
Dany Lozano,Franco Garcia,1956 - PardoA8 CHB MS
Dany Lozano,Franco Garcia,2043 - AlamosE2 CHB MS
Dany Lozano,Franco Garcia,1873 - VillaA1 CHB MS
Dany Lozano,Jair Padilla,1891 - CasuaH2 CHB MS
Dany Lozano,Jair Padilla,2773 - Jaciny5 Chb Ms
Dany Lozano,Jair Padilla,1908 - LadisB12 CHB MS
Dany Lozano,Jair Padilla,2468 - Balta10 Chb Ms
Dany Lozano,Jair Padilla,1987 - CasuaP1 CHB MS
Dany Lozano,Jair Padilla,2184 - CedrosC5 CHB MS
Dany Lozano,Jhonatan Mercedes,1993 - FloridaQ CHB MS
Dany Lozano,Jhonatan Mercedes,2087 - Enrique7 CHB MS
Dany Lozano,Jhonatan Mercedes,2167 - LuisM1 CHB MS
Dany Lozano,Jhonatan Mercedes,2705 - Juliof3 Chb Ms
Dany Lozano,Jhonatan Mercedes,2213 - Nepeña29 CHB MS
Dany Lozano,Jhonatan Mercedes,2187 - OrmeñoJ2 CHB MS
Dany Lozano,Joselyn Cirilo,2116 - VillaD8 CHB MS
Dany Lozano,Joselyn Cirilo,2750 - Abancay1 Chb Ms
Dany Lozano,Joselyn Cirilo,2060 - Huanuco21 CHB MS
Dany Lozano,Joselyn Cirilo,2046 - Ugarte2 CHB MS
Dany Lozano,Joselyn Cirilo,1876 - Porve18 CHB MS
Dany Lozano,Joselyn Cirilo,2028 - Enrique54 CHB MS
Dany Lozano,Mauricio Pando,2297 - Enrique23 Chb Ms
Dany Lozano,Mauricio Pando,2149 - LaderasA1 CHB MS
Dany Lozano,Mauricio Pando,2371 - Calleu1 Chb Ms
Dany Lozano,Mauricio Pando,2342 - Sanluis46 Chb Ms
Dany Lozano,Mauricio Pando,2287 - Delit2 Chb Ms
Dany Lozano,Mauricio Pando,2702 - Belend27 Chb Ms
Dany Lozano,Paulo Cuentas,2529 - Real11 Chb Ms
Dany Lozano,Paulo Cuentas,1836 - Aires7 CHB MS
Dany Lozano,Paulo Cuentas,2181 - Porve24 CHB MS
Dany Lozano,Paulo Cuentas,1835 - Miraf22 CHB MS
Dany Lozano,Paulo Cuentas,2198 - Chinecab1 CHB MS
Dany Lozano,Paulo Cuentas,2288 - Delsurk1 Chb Ms
Dany Lozano,Rina Rios,1979 - Bolivar186 CHB MS
Dany Lozano,Rina Rios,1978 - PaciB1 CHB MS
Dany Lozano,Rina Rios,1955 - Deli16 CHB MS
Dany Lozano,Rina Rios,2596 - Victor37 Chb Ms
Dany Lozano,Rina Rios,2265 - Moro8 Chb Ms
Dany Lozano,Rina Rios,1860 - Ahmoli CHB MS"""
    val assignments: List<StoreAssignment> by lazy {
        csv.lineSequence().filter { it.isNotBlank() }.mapNotNull { line ->
            val p=line.split(",", limit=3)
            if(p.size==3) StoreAssignment(p[0].trim(),p[1].trim(),p[2].trim()) else null
        }.toList()
    }
    val salesLeads get() = assignments.map { it.salesLead }.distinct()
    fun supervisors(lead:String)=assignments.filter{it.salesLead==lead}.map{it.supervisor}.distinct()
    fun stores(lead:String, supervisor:String)=assignments.filter{it.salesLead==lead && it.supervisor==supervisor}.map{it.store}.distinct()
}