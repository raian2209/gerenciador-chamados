<%@ include file="/WEB-INF/jsp/fragments/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="pt-BR">
<%@ include file="/WEB-INF/jsp/fragments/head.jspf" %>
<body data-page="morador-chamados">
<div class="app-shell">
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jspf" %>
    <div class="app-main">
        <%@ include file="/WEB-INF/jsp/fragments/topbar.jspf" %>
        <main class="page-content">
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jspf" %>

            <section class="card">
                <div class="section-header">
                    <div>
                        <p class="eyebrow">Historico</p>
                        <h2>Meus chamados</h2>
                    </div>
                    <a href="${ctx}/morador/chamados/novo" class="btn btn-primary">Abrir chamado</a>
                </div>

                <c:choose>
                    <c:when test="${empty chamados}">
                        <div class="empty-state">
                            <h3>Nenhum chamado registrado</h3>
                            <p>Use a abertura de chamado para registrar a primeira ocorrencia.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                <tr>
                                    <th>Unidade</th>
                                    <th>Tipo</th>
                                    <th>Status</th>
                                    <th>Abertura</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${chamados}" var="chamado">
                                    <tr>
                                        <td>${chamado.unidadeIdentificacao}</td>
                                        <td>${chamado.tipoChamadoTitulo}</td>
                                        <td><span class="status-pill">${chamado.statusNome}</span></td>
                                        <td>${chamado.dataAberturaFormatada}</td>
                                        <td class="cell-actions">
                                            <a href="${ctx}/morador/chamados/${chamado.id}" class="btn btn-link">Acompanhar</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="pagination">
                    <c:if test="${chamadosPage.hasPrevious}">
                        <a class="btn btn-secondary" href="${ctx}/morador/chamados?page=${chamadosPage.page - 1}&size=${chamadosPage.size}">Anterior</a>
                    </c:if>
                    <span>Pagina ${chamadosPage.page + 1} de ${chamadosPage.totalPages == 0 ? 1 : chamadosPage.totalPages}</span>
                    <c:if test="${chamadosPage.hasNext}">
                        <a class="btn btn-secondary" href="${ctx}/morador/chamados?page=${chamadosPage.page + 1}&size=${chamadosPage.size}">Proxima</a>
                    </c:if>
                </div>
            </section>
        </main>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/fragments/scripts.jspf" %>
</body>
</html>
