package com.tamararodrigues.todolist.utils;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class Utils {

        // copyNonNullProperties(metodo que literalmente copia propriedades nao nulas)
    //recebe o objeto "novo"(a atualizacao), e tbm o objeto da BD(taskBD)
    public static void copyNonNullProperties(Object dadosnovos, Object tasksBD){
        //  BeanUtils.copyProperties-> metodo que serve para copiar valores dee um objeto para outro
        //getNullPropertyNames(dadosnovos)->faa a copia e ignora os campos que estao nulls
        BeanUtils.copyProperties(dadosnovos, tasksBD, getNullPropertyNames(dadosnovos));
    }

    //metodo  que pega o objeto vindo da requisicao(task)
    public static String[] getNullPropertyNames(Object dadosnovos){

        //wrapper(embrulha)o objeto para o java inspecionar tdos atributos em tempo de execucao
        final BeanWrapper src = new BeanWrapperImpl(dadosnovos);

        //le a estrutura e retorna um array com as propriedades(atributos)
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        //cria uma lista p/armazenar temporariamente os nomes dos campos com valor null
        Set<String> emptyNames = new HashSet<>();

        //passa por cada propriedade(pd) dentro do array(pds)
        for(PropertyDescriptor pd : pds){
            //pega o valor atual de cada atributo e verifica se esta null
            Object srValue = src.getPropertyValue(pd.getName());
            //se estiver null cai aqui
            if (srValue == null){
                //se estiver null e adicionado a lista
                emptyNames.add(pd.getName());
            }
        }

        //cria uma array de string com o tamanho exato da quant. de nulls encontrados
        String[] result = new String[emptyNames.size()];
        //converte o conjunto Set para array String e retorna. Esse array final serve saber exatamente quais campos nao pode sobrescrever na hr de atualizar
        return emptyNames.toArray(result);

    }


}
