<%@ include file="/WEB-INF/jsp/fragments/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="pt-BR">
<%@ include file="/WEB-INF/jsp/fragments/head.jspf" %>
<body data-page="admin-vinculos-morador">
<div class="app-shell">
    <%@ include file="/WEB-INF/jsp/fragments/sidebar.jspf" %>
    <div class="app-main">
        <%@ include file="/WEB-INF/jsp/fragments/topbar.jspf" %>
        <main class="page-content">
            <%@ include file="/WEB-INF/jsp/fragments/alerts.jspf" %>

            <section class="two-column-grid">
                <article class="card">
                    <div class="section-header">
                        <div>
                            <p class="eyebrow">Operacao</p>
                            <h2>Vincular morador a unidade</h2>
                        </div>
                    </div>

                    <form method="get" action="${ctx}/admin/vinculos-morador" class="stack-form compact-form">
                        <label class="field">
                            <span>Buscar por e-mail</span>
                            <input type="text" name="moradorEmail" value="${filtroMoradorEmail}" placeholder="Ex.: mar" />
                        </label>
                        <label class="field">
                            <span>Morador</span>
                            <select name="moradorId" data-auto-submit>
                                <option value="">Escolha um morador</option>
                                <c:forEach items="${moradoresDisponiveis}" var="morador">
                                    <option value="${morador.id}" ${moradorSelecionadoId eq morador.id ? 'selected' : ''}>
                                        ${morador.nome} - ${morador.email}
                                    </option>
                                </c:forEach>
                            </select>
                        </label>
                        <div class="button-row">
                            <button type="submit" class="btn btn-primary">Buscar morador</button>
                            <a href="${ctx}/admin/vinculos-morador" class="btn btn-secondary">Limpar</a>
                        </div>

                        <c:if test="${not empty moradorSelecionadoId}">
                            <label class="field">
                                <span>Bloco</span>
                                <select name="blocoId" data-auto-submit>
                                    <option value="">Escolha um bloco</option>
                                    <c:forEach items="${blocosDisponiveis}" var="bloco">
                                        <option value="${bloco.id}" ${blocoSelecionadoId eq bloco.id ? 'selected' : ''}>
                                            ${bloco.identificacao}
                                        </option>
                                    </c:forEach>
                                </select>
                            </label>
                        </c:if>
                    </form>

                    <c:if test="${not empty moradorSelecionado}">
                        <div class="divider"></div>
                        <div class="list-row">
                            <div>
                                <strong>${moradorSelecionado.nome}</strong>
                                <span>${moradorSelecionado.email}</span>
                            </div>
                            <a href="${ctx}/admin/usuarios/${moradorSelecionado.id}" class="btn btn-secondary">Abrir cadastro</a>
                        </div>

                        <c:choose>
                            <c:when test="${empty unidadesMorador}">
                                <div class="empty-state compact">
                                    <p>Este morador ainda nao possui unidades vinculadas.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="stack-list">
                                    <c:forEach items="${unidadesMorador}" var="unidade">
                                        <div class="list-row">
                                            <div>
                                                <strong>${unidade.identificacao}</strong>
                                                <span>${unidade.blocoIdentificacao} - Andar ${unidade.andar}</span>
                                            </div>
                                            <form method="post" action="${ctx}/admin/moradores/${moradorSelecionadoId}/unidades/${unidade.id}/desvincular?dashboard=true" data-confirm="Desvincular esta unidade do morador?" class="inline-form">
                                                <%@ include file="/WEB-INF/jsp/fragments/csrf.jspf" %>
                                                <c:if test="${not empty blocoSelecionadoId}">
                                                    <input type="hidden" name="blocoId" value="${blocoSelecionadoId}">
                                                </c:if>
                                                <button type="submit" class="btn btn-danger">Desvincular</button>
                                            </form>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <c:if test="${not empty unidadesBloco}">
                            <div class="divider"></div>
                            <form method="post" action="${ctx}/admin/moradores/${moradorSelecionadoId}/unidades/vincular?dashboard=true" class="stack-form compact-form">
                                <%@ include file="/WEB-INF/jsp/fragments/csrf.jspf" %>
                                <input type="hidden" name="blocoId" value="${blocoSelecionadoId}">
                                <label class="field">
                                    <span>Unidade do bloco selecionado</span>
                                    <select name="unidadeId" required>
                                        <option value="">Escolha uma unidade</option>
                                        <c:forEach items="${unidadesBloco}" var="unidade">
                                            <option value="${unidade.id}" ${unidade.vinculadaAoMorador ? 'disabled' : ''}>
                                                ${unidade.identificacao} - Andar ${unidade.andar}
                                                ${unidade.vinculadaAoMorador ? ' (ja vinculada)' : ''}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <button type="submit" class="btn btn-primary">Vincular morador</button>
                            </form>
                        </c:if>
                    </c:if>
                </article>

                <article class="card">
                    <div class="section-header">
                        <div>
                            <p class="eyebrow">Pendencias</p>
                            <h2>Moradores sem unidade</h2>
                        </div>
                    </div>

                    <form method="get" action="${ctx}/admin/vinculos-morador" class="stack-form compact-form">
                        <c:if test="${not empty moradorSelecionadoId}">
                            <input type="hidden" name="moradorId" value="${moradorSelecionadoId}" />
                        </c:if>
                        <c:if test="${not empty blocoSelecionadoId}">
                            <input type="hidden" name="blocoId" value="${blocoSelecionadoId}" />
                        </c:if>
                        <c:if test="${not empty filtroMoradorEmail}">
                            <input type="hidden" name="moradorEmail" value="${filtroMoradorEmail}" />
                        </c:if>
                        <label class="field">
                            <span>Buscar pendentes por e-mail</span>
                            <input type="text" name="semUnidadeEmail" value="${filtroSemUnidadeEmail}" placeholder="Ex.: mar" />
                        </label>
                        <div class="button-row">
                            <button type="submit" class="btn btn-primary">Buscar pendentes</button>
                        </div>
                    </form>

                    <c:choose>
                        <c:when test="${empty moradoresSemUnidade}">
                            <div class="empty-state">
                                <h3>Nenhum morador pendente</h3>
                                <p>Todos os moradores desta consulta ja possuem unidade vinculada.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-wrap">
                                <table class="data-table" data-filter-table="moradores-sem-unidade-table">
                                    <thead>
                                    <tr>
                                        <th>Nome</th>
                                        <th>Email</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${moradoresSemUnidade}" var="morador">
                                        <c:url var="selecionarMoradorUrl" value="/admin/vinculos-morador">
                                            <c:param name="moradorId" value="${morador.id}" />
                                            <c:if test="${not empty filtroMoradorEmail}">
                                                <c:param name="moradorEmail" value="${filtroMoradorEmail}" />
                                            </c:if>
                                            <c:if test="${not empty filtroSemUnidadeEmail}">
                                                <c:param name="semUnidadeEmail" value="${filtroSemUnidadeEmail}" />
                                            </c:if>
                                        </c:url>
                                        <tr>
                                            <td>${morador.nome}</td>
                                            <td>${morador.email}</td>
                                            <td class="cell-actions">
                                                <a href="${selecionarMoradorUrl}" class="btn btn-link">Selecionar</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <c:url var="paginaAnteriorUrl" value="/admin/vinculos-morador">
                        <c:param name="semUnidadePage" value="${moradoresSemUnidadePage.page - 1}" />
                        <c:param name="semUnidadeSize" value="${moradoresSemUnidadePage.size}" />
                        <c:if test="${not empty moradorSelecionadoId}">
                            <c:param name="moradorId" value="${moradorSelecionadoId}" />
                        </c:if>
                        <c:if test="${not empty blocoSelecionadoId}">
                            <c:param name="blocoId" value="${blocoSelecionadoId}" />
                        </c:if>
                        <c:if test="${not empty filtroMoradorEmail}">
                            <c:param name="moradorEmail" value="${filtroMoradorEmail}" />
                        </c:if>
                        <c:if test="${not empty filtroSemUnidadeEmail}">
                            <c:param name="semUnidadeEmail" value="${filtroSemUnidadeEmail}" />
                        </c:if>
                    </c:url>
                    <c:url var="proximaPaginaUrl" value="/admin/vinculos-morador">
                        <c:param name="semUnidadePage" value="${moradoresSemUnidadePage.page + 1}" />
                        <c:param name="semUnidadeSize" value="${moradoresSemUnidadePage.size}" />
                        <c:if test="${not empty moradorSelecionadoId}">
                            <c:param name="moradorId" value="${moradorSelecionadoId}" />
                        </c:if>
                        <c:if test="${not empty blocoSelecionadoId}">
                            <c:param name="blocoId" value="${blocoSelecionadoId}" />
                        </c:if>
                        <c:if test="${not empty filtroMoradorEmail}">
                            <c:param name="moradorEmail" value="${filtroMoradorEmail}" />
                        </c:if>
                        <c:if test="${not empty filtroSemUnidadeEmail}">
                            <c:param name="semUnidadeEmail" value="${filtroSemUnidadeEmail}" />
                        </c:if>
                    </c:url>

                    <div class="pagination">
                        <c:if test="${moradoresSemUnidadePage.hasPrevious}">
                            <a class="btn btn-secondary" href="${paginaAnteriorUrl}">Anterior</a>
                        </c:if>
                        <span>Pagina ${moradoresSemUnidadePage.page + 1} de ${moradoresSemUnidadePage.totalPages == 0 ? 1 : moradoresSemUnidadePage.totalPages}</span>
                        <c:if test="${moradoresSemUnidadePage.hasNext}">
                            <a class="btn btn-secondary" href="${proximaPaginaUrl}">Proxima</a>
                        </c:if>
                    </div>
                </article>
            </section>
        </main>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/fragments/scripts.jspf" %>
</body>
</html>
