package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.componentes.DonadoresYEntidadesClient;
import org.springframework.context.annotation.Configuration;

/**
 * Conecta el Fachada de Incentivos con los adapters REST de los otros módulos.
 *
 * Spring inyecta el Fachada y los dos proxies (todos beans singleton) y, en el constructor,
 * se ejecutan los setters una sola vez al levantar la app. Sin esto, fachadaDonadoresYEntidades
 * y fachadaDonaciones quedan en null y todo lo que dependa de otros módulos rompe.
 */
@Configuration
public class IntegracionFachadas {

    public IntegracionFachadas(
            Fachada fachada,
            FachadaDonadoresYEntidades donadoresYEntidades,
            FachadaDonaciones donaciones,
            DonadoresYEntidadesClient donadoresYEntidadesClient) {
        fachada.setFachadaDonadoresYEntidades(donadoresYEntidades);
        fachada.setFachadaDonaciones(donaciones);
        fachada.setDonadoresYEntidadesClient(donadoresYEntidadesClient);
    }
}
