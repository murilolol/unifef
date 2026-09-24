$(document).ready(function(){
    console.log('Ativo evento focus campo cpf');
    $('#cpfcnpjpessoa').focus(function(){
        trocaMascaraCpfCnpj("A");
    });
});

$(document).ready(function(){
    console.log('Ativo evento blur campo cpf');
    $('#cpfcnpjpessoa').blur(function(){
       var cpfCnpjLimpo = $('#cpfcnpjpessoa').unmask().val();
       if (!validarCpfCnpj(cpfCnpjLimpo)){
            Swal.fire({
                position: 'center',
                icon: 'error',
                title: 'Verifique o CPF/CNPJ!',
                showConfirmButton: true,
                timer: 10000
            });
        }else{
           trocaMascaraCpfCnpj($('#cpfcnpjpessoa').val());
       }
    });
});

function trocaMascaraCpfCnpj(cpfCnpj)
{
    if (cpfCnpj !== "A")
    {
        var masks = ['999.999.999-99', '99.999.999/9999-99'];
        var cpfcnpj = $('#cpfcnpjpessoa').unmask().val();
        var mask = (cpfcnpj.length > 11) ? masks[1] : masks[0];
        $('#cpfcnpjpessoa').mask(mask);
    }
    else{
       $('#cpfcnpjpessoa').unmask();
    }
};

function validarCpfCnpj(cpfCnpj){
    if(cpfCnpj.length === 14){
        return true;
    }else{
        return cpfCnpj.length === 11;
    }
}
