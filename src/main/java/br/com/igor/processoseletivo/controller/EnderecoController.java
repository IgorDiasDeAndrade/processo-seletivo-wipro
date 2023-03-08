package br.com.igor.processoseletivo.controller;
import br.com.igor.processoseletivo.modelo.Cep;
import br.com.igor.processoseletivo.modelo.Endereco;
import br.com.igor.processoseletivo.modelo.EnderecoResposta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/v1/consulta-endereco")
public class EnderecoController {

    @PostMapping()
    public EnderecoResposta endereco(@RequestBody Cep cep) {
        String resposta = cep.getCep();
        float frete = 0.0f;

        if (consumeAPI(resposta).getCep() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (consumeAPI(resposta).getDdd() >= 11 && consumeAPI(resposta).getDdd() <= 38){
            frete = 7.85f;
        } else if (consumeAPI(resposta).getDdd() >= 44 && consumeAPI(resposta).getDdd() <= 53 ){
            frete = 17.30f;
        } else if (consumeAPI(resposta).getDdd() >= 65 && consumeAPI(resposta).getDdd() <= 67 ){
            frete = 12.50f;
        } else if (consumeAPI(resposta).getDdd() >= 73 && consumeAPI(resposta).getDdd() <= 86 ){
            frete = 15.98f;
        } else if (consumeAPI(resposta).getDdd() == 98 || consumeAPI(resposta).getDdd() == 99 ){
            frete = 15.98f;
        } else if (consumeAPI(resposta).getDdd() == 68 || consumeAPI(resposta).getDdd() == 69 ){
            frete = 12.50f;
        } else if (consumeAPI(resposta).getDdd() >= 91 && consumeAPI(resposta).getDdd() <= 97 ){
            frete = 20.83f;
        }


        return new EnderecoResposta(consumeAPI(resposta).getCep(),
                consumeAPI(resposta).getLogradouro(),
                consumeAPI(resposta).getComplemento(),
                consumeAPI(resposta).getBairro(),
                consumeAPI(resposta).getLocalidade(),
                consumeAPI(resposta).getUf(),
                frete);
    }

    public Endereco consumeAPI(String resposta){
        RestTemplate template = new RestTemplate();
        UriComponents uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("viacep.com.br")
                .path("ws/"+resposta+"/json/")
                .build();

        ResponseEntity<Endereco> entity = template.getForEntity(uri.toUriString(), Endereco.class);



        return entity.getBody();
    }
}