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

        Map<Tipo,Integer> quantidadeOcorrenciaTipo = new HashMap<>();

        Map<String,List<Long>> temposMediosPorAtendente = new HashMap<>();
        Map<String,Evento> ultimoEventoAlarmePorCliente = new HashMap<>();

        List<String> linhas = FileUtils.readLines(path, "UTF-8");

        for(String linha : linhas) {

            Evento evento = carregaArquivo(linha);

            agrupaEventoPorCliente(evento);
            agrupaQuantidadeOcorrenciaPorEvento(quantidadeOcorrenciaTipo,evento);
            agrupaAtendenteTemposMedios(temposMediosPorAtendente,evento);
            processaEventoAlarmeDesarme(ultimoEventoAlarmePorCliente,evento);
            atualizaUltimoEvento(ultimoEventoAlarmePorCliente,evento);
        }

        processaQuantidadeOcorrenciaPorTipo(quantidadeOcorrenciaTipo);
        descobreTemposMedios(temposMediosPorAtendente);
    }

    /**
     * Atualiza o mapa de ultimo evento por cliente, caso seja um evento de alarme.
     * @param ultimoEventoAlarmePorCliente - Mapa para guardar o ultimo evento de alarme de cada cliente
     * @param evento - Evento sendo atualmente processado
     */
    private void atualizaUltimoEvento(Map<String, Evento> ultimoEventoAlarmePorCliente, Evento evento) {

        if(evento.getTipoEvento().equals(Tipo.ALARME)) {
            ultimoEventoAlarmePorCliente.put(evento.getCodigoCliente(),evento);
        }
    }


    /**
     * Agrupa os eventos por cada cliente para preecher o mapa de eventosCliente
     * @param evento - Evento sendo atualmente processado
     */

    private void agrupaEventoPorCliente(Evento evento){

        Integer quantidadeEventos = 1;

        if (eventosCliente.containsKey(evento.getCodigoCliente())) {
            quantidadeEventos = eventosCliente.get(evento.getCodigoCliente());
            quantidadeEventos++;
        }

        eventosCliente.put(evento.getCodigoCliente(),quantidadeEventos);

    }

    /**
     * Agrupa a quantidade de ocorrencia por cada tipo de evento para depois ordenar
     * @param quantidadeOcorrenciaTipo - Mapa com o agrupamento de Tipo de evento com a quantidade de ocorrencias
     * @param evento - Evento sendo atualmente processado
     */
    private void agrupaQuantidadeOcorrenciaPorEvento(Map<Tipo,Integer> quantidadeOcorrenciaTipo, Evento evento) {

        Integer tipoQuantidade = 1;
        if (quantidadeOcorrenciaTipo.containsKey(evento.getTipoEvento())) {
            tipoQuantidade = quantidadeOcorrenciaTipo.get(evento.getTipoEvento());
            tipoQuantidade++;
        }

        quantidadeOcorrenciaTipo.put(evento.getTipoEvento(), tipoQuantidade);
    }

    /**
     * Agrupa para cada atendente a lista de tempos de duracao de cada evento
     * @param temposMediosPorAtendente - Mapa com o agrupamento de atendente com os seus tempos
     * @param evento - Evento sendo atualmente processado
     */
    private void agrupaAtendenteTemposMedios(Map<String,List<Long>> temposMediosPorAtendente, Evento evento) {

        List<Long> temposMedios = new ArrayList<>();
        if (temposMediosPorAtendente.containsKey(evento.getCodigoAtendente())) {
            temposMedios = temposMediosPorAtendente.get(evento.getCodigoAtendente());
        }

        temposMedios.add(evento.getDiferencaFimInicio());
        temposMediosPorAtendente.put(evento.getCodigoAtendente(), temposMedios);

    }

    /**
     * Processa o evento de alarme seguido por desarme
     * Verifica se o evento sendo processado e de desarme. Caso seja verifica-se o ultimo evento de alarme do mesmo cliente. Caso o tempo seja ate 5 minutos guarda o codigo sequencial
     * @param ultimoEventoAlarmePorCliente - O ultimo evento de Alarma de um determinado cliente
     * @param evento - Evento sendo atualmente processado
     */
    private void processaEventoAlarmeDesarme(Map<String,Evento> ultimoEventoAlarmePorCliente, Evento evento) {

        if (ultimoEventoAlarmePorCliente != null && evento.getTipoEvento().equals(Tipo.DESARME)) {
            Evento ultimoEvento = ultimoEventoAlarmePorCliente.get(evento.getCodigoCliente());

            if(ultimoEvento!=null){

                int diferencaMinutos = Minutes.minutesBetween(ultimoEvento.getDataInicio(),evento.getDataInicio()).getMinutes();
                if(diferencaMinutos>=0 && diferencaMinutos<=5){
                    codigoSequencialEventosDesarme.add(evento.getCodigoSequencial());
                }
            }
        }
    }

    /**
     *  Processo o mapa de quantidadeOcorrenciaTipo para identificar a ordem decrescente dos eventos.
     *  Itera o mapa de quantidade de ocorrencia por tipo para uma lista de uma classe que possui uma comparacao por quantidade.
     *  Atraves da implementacao de Comparable<QuantidadeTipoHolder> permite ordenar por ordem crescente e posteriormente reverte a lista para ordem decrescente.
     *  Por fim itera a lista em ordem decrescente pegando os tipos que QuantidadeTipoHolder possui
     *
     * @param quantidadeOcorrenciaTipo - O mapa de quantidade de ocorrencia por tipo
     */
    private void processaQuantidadeOcorrenciaPorTipo(Map<Tipo,Integer> quantidadeOcorrenciaTipo){

        List<QuantidadeTipoHolder> quantidadeTipoHolderList = new ArrayList<>();

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
    }

    /**
     * Atraves do mapa de tempos por atendente, descobre-se o tempo medio de cada atendente e dividi-se pelo total de eventos.
     * @param temposMediosPorAtendente - Mapa de tempos por atendente
     */
    private void descobreTemposMedios(Map<String,List<Long>> temposMediosPorAtendente){

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