package br.com.segware;

import org.apache.commons.io.FileUtils;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AnalisadorRelatorioImpl implements IAnalisadorRelatorio {

    private Map<String,Integer> eventosCliente = new HashMap<>();
    private List<Tipo> tiposOrdenados = new ArrayList<>();
    private Map<String,Long> tempoMedio = new HashMap<>();
    private List<Integer> codigoSequencialEventosDesarme = new ArrayList<>();

    public void processaArquivo(File path) throws IOException {

        List<QuantidadeTipoHolder> quantidadeTipoHolderList = new ArrayList<>();
        Map<Tipo,Integer> quantidadeOcorrenciaTipo = new HashMap<>();
        
        Map<String,List<Long>> temposMediosPorAtendente = new HashMap<>();
        Map<String,Evento> ultimoEventoAlarmePorCliente = new HashMap<>();

        List<String> linhas = FileUtils.readLines(path, "UTF-8");

        for(String linha : linhas) {

            Integer quantidadeEventos = 1;
            Integer tipoQuantidade = 1;

            Evento evento = carregaArquivo(linha);

            if (eventosCliente.containsKey(evento.getCodigoCliente())) {
                quantidadeEventos = eventosCliente.get(evento.getCodigoCliente());
                quantidadeEventos++;
            }

            eventosCliente.put(evento.getCodigoCliente(),quantidadeEventos);

            if (quantidadeOcorrenciaTipo.containsKey(evento.getTipoEvento())) {
                tipoQuantidade = quantidadeOcorrenciaTipo.get(evento.getTipoEvento());
                tipoQuantidade++;
            }

            quantidadeOcorrenciaTipo.put(evento.getTipoEvento(),tipoQuantidade);

            List<Long> temposMedios = new ArrayList<>();
            if (temposMediosPorAtendente.containsKey(evento.getCodigoAtendente())) {
                temposMedios = temposMediosPorAtendente.get(evento.getCodigoAtendente());
            }

            temposMedios.add(evento.getDiferencaFimInicio());
            temposMediosPorAtendente.put(evento.getCodigoAtendente(),temposMedios);

            if (ultimoEventoAlarmePorCliente != null && evento.getTipoEvento().equals(Tipo.DESARME)) {
                Evento ultimoEvento = ultimoEventoAlarmePorCliente.get(evento.getCodigoCliente());

                if(ultimoEvento!=null){
                    int diferencaMinutos = Minutes.minutesBetween(ultimoEvento.getDataInicio(),evento.getDataInicio()).getMinutes();
                    if(diferencaMinutos>=0 && diferencaMinutos<=5){
                        codigoSequencialEventosDesarme.add(evento.getCodigoSequencial());
                    }
                }
            }

            if(evento.getTipoEvento().equals(Tipo.ALARME)) {
                ultimoEventoAlarmePorCliente.put(evento.getCodigoCliente(),evento);
            }

        }

        for(Map.Entry<Tipo, Integer> entry : quantidadeOcorrenciaTipo.entrySet()) {
            Tipo tipo = entry.getKey();
            Integer value = entry.getValue();
            QuantidadeTipoHolder quantidadeTipoHolder = new QuantidadeTipoHolder(value,tipo);
            quantidadeTipoHolderList.add(quantidadeTipoHolder);
        }

        Collections.sort(quantidadeTipoHolderList);
        Collections.reverse(quantidadeTipoHolderList);

        for(QuantidadeTipoHolder valor:quantidadeTipoHolderList) {
            tiposOrdenados.add(valor.getTipo());
        }

        for(Map.Entry<String, List<Long>> entry : temposMediosPorAtendente.entrySet()) {
            String atendente = entry.getKey();
            List<Long> temposMedios = entry.getValue();
            Long total = new Long(0);
            for(Long tempoMedio:temposMedios) {
                total += tempoMedio;
            }
            tempoMedio.put(atendente,total / temposMedios.size());
        }
    }

    @Override
    public Map<String, Integer> getTotalEventosCliente() {
        return eventosCliente;
    }

    @Override
    public Map<String, Long> getTempoMedioAtendimentoAtendente() {
        return tempoMedio;
    }

    @Override
    public List<Tipo> getTiposOrdenadosNumerosEventosDecrescente() {
        return tiposOrdenados;
    }

    @Override
    public List<Integer> getCodigoSequencialEventosDesarmeAposAlarme() {
        return codigoSequencialEventosDesarme;
    }


    public Evento carregaArquivo(String linha) {

        Evento evento = new Evento();
        String[] colunas = linha.split(",");
        int index;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        for(index=0;index<colunas.length;index++){
            String valor = colunas[index];

            switch (index){

                case 0:
                    evento.setCodigoSequencial(new Integer(valor));
                    break;
                case 1:
                    evento.setCodigoCliente(valor);
                    break;
                case 2:
                    evento.setCodigoEvento(valor);
                    break;
                case 3:
                    evento.setTipoEvento(Tipo.valueOf(valor));
                    break;
                case 4:
                    evento.setDataInicio(formatter.parseLocalDateTime(valor));
                    break;
                case 5:
                    evento.setDataFim(formatter.parseLocalDateTime(valor));
                    break;
                case 6:
                    evento.setCodigoAtendente(valor);
                    break;

            }

        }
        return evento;
    }
}
